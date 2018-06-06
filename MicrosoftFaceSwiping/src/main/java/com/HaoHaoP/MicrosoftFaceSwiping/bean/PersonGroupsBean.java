package com.HaoHaoP.MicrosoftFaceSwiping.bean;

public class PersonGroupsBean {

    /**
     * personGroupId : sample_group
     * name : group1
     * userData : User-provided data attached to the person group.
     */

    private String personGroupId;
    private String name;
    private String userData;

    public String getPersonGroupId() {
        return personGroupId;
    }

    public void setPersonGroupId(String personGroupId) {
        this.personGroupId = personGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }
}
