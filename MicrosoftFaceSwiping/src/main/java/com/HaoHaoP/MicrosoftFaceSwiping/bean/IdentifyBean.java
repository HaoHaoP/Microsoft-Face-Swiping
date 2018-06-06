package com.HaoHaoP.MicrosoftFaceSwiping.bean;

import java.util.List;

public class IdentifyBean {

    /**
     * faceId : ef72b2d0-407b-4ad0-86aa-7df33b666e4f
     * candidates : [{"personId":"4075040c-c74c-4c40-a622-42180eed16ba","confidence":0.83044}]
     */

    private String faceId;
    private List<CandidatesBean> candidates;

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public List<CandidatesBean> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidatesBean> candidates) {
        this.candidates = candidates;
    }

    public static class CandidatesBean {
        /**
         * personId : 4075040c-c74c-4c40-a622-42180eed16ba
         * confidence : 0.83044
         */

        private String personId;
        private double confidence;

        public String getPersonId() {
            return personId;
        }

        public void setPersonId(String personId) {
            this.personId = personId;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }
    }
}
