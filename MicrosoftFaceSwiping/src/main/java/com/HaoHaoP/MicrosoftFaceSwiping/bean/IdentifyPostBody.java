package com.HaoHaoP.MicrosoftFaceSwiping.bean;

import java.util.List;

public class IdentifyPostBody {
    public List<String> faceIds;

    public String personGroupId;

    public List<String> getFaceIds() {
        return faceIds;
    }

    public void setFaceIds(List<String> faceIds) {
        this.faceIds = faceIds;
    }

    public String getPersonGroupId() {
        return personGroupId;
    }

    public void setPersonGroupId(String personGroupId) {
        this.personGroupId = personGroupId;
    }
}
