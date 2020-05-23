package com.nessathon.dto;

public enum MediaType {

    HTML,
    PDF,
    ZIP,
    TXT,
    WORD,
    EXCEL,
    POWERPOINT,
    IMAGE,
    AUDIO,
    VIDEO,
    OTHER;

    public static MediaType getType(String extension) {

        if (extension == null) {
            return MediaType.OTHER;
        } else if (extension.matches("(?i)(HTML)|(HTM)")) {
            return MediaType.HTML;
        } else if (extension.matches("(?i)(PDF)")) {
            return MediaType.PDF;
        } else if (extension.matches("(?i)(ZIP)|(RAR)")) {
            return MediaType.ZIP;
        } else if (extension.matches("(?i)(TXT)")) {
            return MediaType.TXT;
        } else if (extension.matches("(?i)(DOCX)")) {
            return MediaType.WORD;
        } else if (extension.matches("(?i)(XLSX)")) {
            return MediaType.EXCEL;
        } else if (extension.matches("(?i)(PPTX)")) {
            return MediaType.POWERPOINT;
        } else if (extension.matches("(?i)(JPG)|(JPEG)|(PNG)")) {
            return MediaType.IMAGE;
        } else if (extension.matches("(?i)(MP3)")) {
            return MediaType.AUDIO;
        } else if (extension.matches("(?i)(MP4)|(FLV)|(MOV)")) {
            return MediaType.VIDEO;
        } else {
            return MediaType.OTHER;
        }
    }
}
