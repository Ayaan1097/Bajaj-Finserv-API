package com.bajajfinserv.model;

public class QuestionMetadata {
    private int questionId;
    private String sourceUrl;
    private String problemDescription;

    public QuestionMetadata(int questionId, String sourceUrl, String problemDescription) {
        this.questionId = questionId;
        this.sourceUrl = sourceUrl;
        this.problemDescription = problemDescription;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getProblemDescription() {
        return problemDescription;
    }
}
