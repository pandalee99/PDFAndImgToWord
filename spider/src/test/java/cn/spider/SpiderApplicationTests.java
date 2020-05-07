package cn.spider;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class SpiderApplicationTests {

    @Test
    void contextLoads() {
        for (int i = 1; i<7 ; i++) {
            yzm(i);
        }
//        yzm(11);

    }


    void yzm(int i){

        //验证码图片存储地址
        File imageFile = new File("E:\\Downloads\\"+i+".jpg");
        if(!imageFile.exists()){
            System.out.println("图片不存在");;
        }
        Tesseract tessreact = new Tesseract();
        tessreact.setDatapath("E:\\Temp\\tessdata");
        tessreact.setLanguage("chi_sim");
        String result;
        try {
            result = "测验结果：" + tessreact.doOCR(imageFile);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
