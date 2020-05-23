package com.nessathon.dto;

import java.util.List;

public class Artefact {

    private int id;

    private String name;

    private String urlKey;

    private MediaType mediaType;

    private long sizeInBytes;

    private int parentArtefactID;

    private List<Artefact> transcodedArtefacts;

    boolean isTranscodeComplete;

    public Artefact(String name, String urlKey) {
        this.name = name;
        this.urlKey = urlKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public int getParentArtefactID() {
        return parentArtefactID;
    }

    public void setParentArtefactID(int parentArtefactID) {
        this.parentArtefactID = parentArtefactID;
    }

    public List<Artefact> getTranscodedArtefacts() {
        return transcodedArtefacts;
    }

    public void setTranscodedArtefacts(List<Artefact> transcodedArtefacts) {
        this.transcodedArtefacts = transcodedArtefacts;
    }

    public boolean isTranscodeComplete() {
        return isTranscodeComplete;
    }

    public void setTranscodeComplete(boolean transcodeComplete) {
        isTranscodeComplete = transcodeComplete;
    }
}
