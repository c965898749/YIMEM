package com.sy.model.game;

public class ImageLevelResult {
    // 当前数字在图中的位置（第几位，1-7）
    private final int positionInImage;
    // 当前图片的所有数字数组
    private final int[] currentImageNumbers;
    // 下一张图片的所有数字数组（无则为空数组）
    private final int[] nextImageNumbers;

    public ImageLevelResult(int positionInImage, int[] currentImageNumbers, int[] nextImageNumbers) {
        this.positionInImage = positionInImage;
        this.currentImageNumbers = currentImageNumbers;
        this.nextImageNumbers = nextImageNumbers;
    }

    // Getter方法
    public int getPositionInImage() {
        return positionInImage;
    }

    public int[] getCurrentImageNumbers() {
        return currentImageNumbers;
    }

    public int[] getNextImageNumbers() {
        return nextImageNumbers;
    }
}
