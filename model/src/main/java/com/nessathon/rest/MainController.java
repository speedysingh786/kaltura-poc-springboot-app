package com.nessathon.rest;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nessathon.service.VirusScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nessathon.dto.Artefact;
import com.nessathon.dto.CommonRequest;
import com.nessathon.dto.LoginRequest;
import com.nessathon.dto.MediaRequest;
import com.nessathon.dto.UserAuthenticationStatus;
import com.nessathon.service.ArtefactService;
import com.nessathon.service.AuthenticationService;
import com.nessathon.service.S3ServiceImpl;

@RestController
public class MainController {

    @Autowired
    private S3ServiceImpl s3ServiceImpl;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ArtefactService artefactService;

    @Autowired
    VirusScanService virusScanService;

    @RequestMapping(value = "/validateLogin", method = RequestMethod.POST, consumes = {
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserAuthenticationStatus> validateLogin(@RequestBody LoginRequest loginRequest,
                                                                  HttpServletRequest request, HttpServletResponse response) {

        UserAuthenticationStatus status = null;

        try {

            boolean validLogin = authenticationService.validateLogin(loginRequest.getUsername(),
                    loginRequest.getPassword());

            storeSessionVariables(request, loginRequest.getUsername());

            status = new UserAuthenticationStatus(validLogin ? "Success" : "Failure");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<UserAuthenticationStatus>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {

        request.getSession().invalidate();
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    @RequestMapping(value = "/app/artefacts", method = RequestMethod.POST, consumes = {
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Artefact>> getAllArtefacts(@RequestBody CommonRequest commonRequest) {

        return new ResponseEntity<List<Artefact>>(artefactService.getAllArtefactsShallow(commonRequest.getObject()), HttpStatus.OK);
    }

    @RequestMapping(value = "/app/artefact", method = RequestMethod.POST, consumes = {
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Artefact> getArtefact(@RequestBody CommonRequest commonRequest) {

        return new ResponseEntity<Artefact>(artefactService.getArtefact(commonRequest.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/app/isArtefactTranscodeComplete", method = RequestMethod.POST, consumes = {
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> isArtefactTranscodeComplete(@RequestBody CommonRequest commonRequest) {

        boolean isArtefactTranscodeComplete = "Y".equals(artefactService.getArtefactStatus(commonRequest.getId()));
        return new ResponseEntity<Boolean>(isArtefactTranscodeComplete, HttpStatus.OK);
    }

    @RequestMapping(value = "/app/upload", method = RequestMethod.POST)
    public ResponseEntity<?> uploadDocsArtefacts(@RequestParam("file") MultipartFile uploadfile) throws InterruptedException {
        String objcetUrl = null;
    	List<String> keys = new ArrayList<>();
        if (uploadfile.isEmpty()) {
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }
        try {

            /*if (virusScanService.scan(uploadfile)) {
                return new ResponseEntity(Boolean.FALSE, new HttpHeaders(), HttpStatus.OK);
            }*/

        	String Filename=uploadfile.getOriginalFilename();
			long sizeInBytes = uploadfile.getSize();
            Artefact artefact = artefactService.insertArtefact(uploadfile.getOriginalFilename(), new ObjectMapper().writeValueAsString(keys),Filename, sizeInBytes, -1);

            s3ServiceImpl.uploadDocsToS3Async(Arrays.asList(uploadfile),Filename, artefact.getId());

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

		return new ResponseEntity(Boolean.TRUE, new HttpHeaders(), HttpStatus.OK);

    }

    @RequestMapping(value = "/app/get/secure/url", method = RequestMethod.POST, consumes = {
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<MediaRequest> getS3SecuredUrl(@RequestBody MediaRequest mediaRequest) {

		URL url = null;
		try {
			url = s3ServiceImpl.getS3SecuredUrl(mediaRequest.getKey());
			mediaRequest.setUrl(url.toString());
		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(mediaRequest, new HttpHeaders(), HttpStatus.OK);
	}

    private void storeSessionVariables(HttpServletRequest request, String username) {

        request.getSession().setAttribute("validatedUsername", username);
    }
}