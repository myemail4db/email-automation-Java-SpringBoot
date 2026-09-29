package com.example.email_automation.model;

public class SendWorkflowResult {
   
    private boolean zipCreated;
    private boolean emailSent;
    private boolean filesArchived;
    private int filesArchivedCount;

    public boolean isZipCreated() {
        return zipCreated;
    }

    public void setZipCreated(boolean zipCreated) {
        this.zipCreated = zipCreated;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public boolean isFilesArchived() {
        return filesArchived;
    }

    public void setFilesArchived(boolean filesArchived) {
        this.filesArchived = filesArchived;
    }

    public int getFilesArchivedCount() {
        return filesArchivedCount;
    }

    public void setFilesArchivedCount(int filesArchivedCount) {
        this.filesArchivedCount = filesArchivedCount;
    }    
}
