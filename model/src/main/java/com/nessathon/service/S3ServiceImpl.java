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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Service
public class S3ServiceImpl {

    AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    String S3_BUCKET = "ness-hackothon-bucket";
    String rootKey = "evidence/";

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
        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; // next pls
            }

            bytes = file.getBytes();
        }
        if (filename.split(Pattern.quote("."))[1].equalsIgnoreCase("pdf")) {
            finalKey = rootKey + "docs/" + filename;
        } else {
            finalKey = rootKey + "media/" + filename;
        }

        InputStream is = new ByteArrayInputStream(bytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        PutObjectRequest request = new PutObjectRequest(S3_BUCKET, finalKey, is, metadata);

        PutObjectResult putObjectResult = s3.putObject(request);
        //URL url=s3.getUrl(S3_BUCKET,  rootKey + "docs/" + filename);

        String filestr = filename.split(Pattern.quote("."))[0];
        String convertedfile = null;
        if (filename.split(Pattern.quote("."))[1].equalsIgnoreCase("pdf")) {
            convertedfile = rootKey + "docs/transcoded/" + filestr;
        } else {
            convertedfile = rootKey + "media/transcoded/" + filestr;
        }

        System.out.println("convertedfile:" + convertedfile);
        List<String> finalList = getS3ObjectList(convertedfile, artefactID);
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
