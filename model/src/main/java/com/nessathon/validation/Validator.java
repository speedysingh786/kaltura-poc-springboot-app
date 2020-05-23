package com.nessathon.validation;

public class Validator {

    public static AppError validate(double min, double max, int classes) {

        if (classes >= max - min) {

            return new AppError("TOO_MANY_CLASSES", String.format("Too many classes specified : %s Classes for Total Range %s - %s",
                    classes, min, max));
        }

        return null;
    }
}
