package com.nessathon.service;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class VirusScanService {

    public boolean scan(MultipartFile multipartFile) throws IOException {

        File file = File.createTempFile("tempfile", ".tmp");

        FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());

        String scanCommand = "clamdscan --log=" + FileUtils.getTempDirectory() + "/r.txt" + file.getAbsolutePath();

        Process process = Runtime.getRuntime().exec(scanCommand);

        String report = FileUtils.readFileToString(new File(FileUtils.getTempDirectory() + "/r.txt"));

        return !report.contains("##virus##");
    }

}
