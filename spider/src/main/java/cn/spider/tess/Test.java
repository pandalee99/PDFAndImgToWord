package cn.spider.tess;



import java.io.File;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Test {
    public static void main(String[] args) {

        //验证码图片存储地址
        File imageFile = new File("E:\\Downloads\\1.jpg");
        if(!imageFile.exists()){
            System.out.println("图片不存在");;
        }
        Tesseract tessreact = new Tesseract();
        tessreact.setDatapath("E:\\Temp\\tessdata");

        String result;
        try {
            result = "测验结果：" + tessreact.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            e.printStackTrace();
        }

    }
}
