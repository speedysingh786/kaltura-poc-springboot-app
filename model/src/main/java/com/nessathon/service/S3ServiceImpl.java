package com.nessathon.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Service
public class S3ServiceImpl {

    AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    String S3_BUCKET = "esp-smk-fe-external";
    String rootKey = "test/";

    @Autowired
    ArtefactService artefactService;

    public void uploadDocsToS3Async(List<MultipartFile> files, String filename, int artefactID) throws IOException, InterruptedException {

        CompletableFuture.runAsync(() -> {
            try {
                uploadDocsToS3(files, filename, artefactID);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void uploadDocsToS3(List<MultipartFile> files, String filename, int artefactID) throws IOException, InterruptedException {

        byte[] bytes = null;
        String finalKey = null;
        ArrayList<String> docExtensions = new ArrayList<>(Arrays.asList("pdf","doc","docx","odt","xls","xlsx","ppt","pptx","odt","ods","odp"));
        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; // next pls
            }

            bytes = file.getBytes();
        }
        if (docExtensions.contains(filename.split(Pattern.quote("."))[1])) {
            finalKey = rootKey + "docs/inputs/" + filename;
        } else {
            finalKey = rootKey + "media/inputs/" + filename;
        }

        InputStream is = new ByteArrayInputStream(bytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        PutObjectRequest request = new PutObjectRequest(S3_BUCKET, finalKey, is, metadata);

        PutObjectResult putObjectResult = s3.putObject(request);
        //URL url=s3.getUrl(S3_BUCKET,  rootKey + "docs/" + filename);

        String filestr = filename.split(Pattern.quote("."))[0];
        String convertedfile = null;
        List<String> finalList = new ArrayList<>();
        if (docExtensions.contains(filename.split(Pattern.quote("."))[1])) {
            convertedfile = rootKey + "docs/outputs/windows_" + filestr;
            finalList = getS3ObjectList(convertedfile, artefactID);
            String convertedfileUbuntu = rootKey + "docs/outputs/ubuntu_" + filestr;
            finalList.addAll(getS3ObjectList(convertedfileUbuntu, artefactID));
        } else {
            convertedfile = rootKey + "media/outputs/transcoded_" + filestr;
            finalList = getS3ObjectList(convertedfile, artefactID);
        }

        System.out.println("convertedfile:" + convertedfile);
        
        finalList.add(0, finalKey);

        artefactService.updateArtefactURL(artefactID, finalKey);//parent media
    }

    private List<String> getS3ObjectList(String pathToImpReqId, int artefactID) throws InterruptedException {
        List<String> keys = new ArrayList<>();

        Thread.sleep(30000);
        ObjectListing objects = s3.listObjects(new ListObjectsRequest().withBucketName(S3_BUCKET).withPrefix(pathToImpReqId));
        if (objects != null) {
            do {
                for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                    keys.add(objectSummary.getKey());
                    System.out.println("Keys for IR on S3:{}" + objectSummary.getKey());

                    String fileName = objectSummary.getKey().substring(objectSummary.getKey().lastIndexOf("/") + 1);
                    artefactService.insertArtefact(fileName, objectSummary.getKey(), fileName, objectSummary.getSize(), artefactID);
                }
                objects = s3.listNextBatchOfObjects(objects);
            } while (objects.isTruncated());

            artefactService.updateArtefactStatus(artefactID);
        }
        return keys;
    }

    public URL getS3SecuredUrl(String key) {

        // Set the presigned URL to expire after one hour.
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 30;
        expiration.setTime(expTimeMillis);

        // Generate the presigned URL.
        System.out.println("Generating pre-signed URL.");
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(S3_BUCKET, key)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);

        System.out.println("Pre-Signed URL: " + url.toString());
        return url;

    }

}
