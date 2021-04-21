package com.nessathon.service;

import com.nessathon.dao.DataAccess;
import com.nessathon.dto.Artefact;
import com.nessathon.dto.MediaType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class ArtefactService {

    @Autowired
    private Environment env;

    @Autowired
    DataAccess dataAccess;

    public List<Artefact> getAllArtefactsShallow(String searchParam) {

        return dataAccess.getAllArtefactsShallow(searchParam);
    }

    public Artefact getArtefact(int artefactID) {

        return dataAccess.getArtefact(artefactID);
    }

    public Artefact insertArtefact(String name, String url, String originalFileName, long sizeInBytes, int parentArtefactID) {

        String extension = FilenameUtils.getExtension(originalFileName);
        MediaType mediaType = MediaType.getType(extension);

        Artefact artefact = new Artefact(name, url);
        int artefactID = getNextArtefactSequenceID() + 1;
        artefact.setId(artefactID);
        artefact.setMediaType(mediaType);
        artefact.setSizeInBytes(sizeInBytes);
        artefact.setParentArtefactID(parentArtefactID);

        dataAccess.insertArtefact(artefact);

        return artefact;
    }

    public void updateArtefactURL(int artefactID, String url) {

        dataAccess.updateArtefactURL(artefactID, url);
    }

    public void updateArtefactStatus(int artefactID) {

        dataAccess.updateArtefactStatus(artefactID);
    }

    public String getArtefactStatus(int artefactID) {

        return dataAccess.getArtefactStatus(artefactID);
    }

    private int getNextArtefactSequenceID() {

        return dataAccess.getNextArtefactSequenceID();
    }

}
