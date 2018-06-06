package com.HaoHaoP.MicrosoftFaceSwiping.bean;

public class DetectBean {
    /**
     * faceId : f6400413-d211-432a-9180-737f890ab93b
     * faceRectangle : {"top":127,"left":87,"width":106,"height":106}
     */

    private String faceId;
    private FaceRectangleBean faceRectangle;

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public FaceRectangleBean getFaceRectangle() {
        return faceRectangle;
    }

    public void setFaceRectangle(FaceRectangleBean faceRectangle) {
        this.faceRectangle = faceRectangle;
    }

    public static class FaceRectangleBean {
        /**
         * top : 127
         * left : 87
         * width : 106
         * height : 106
         */

        private int top;
        private int left;
        private int width;
        private int height;

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
