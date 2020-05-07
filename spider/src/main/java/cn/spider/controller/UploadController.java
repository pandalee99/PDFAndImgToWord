package cn.spider.controller;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;


@Controller
public class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @GetMapping("/")
    public String index() {
        return "redirect:upload";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        String fileName = file.getOriginalFilename();
        String just[];
        just=fileName.split("\\.");
        if (! "jpg".equals(just[1]) && !"png".equals(just[1])){
            return "格式错误";
        }
        String filePath = "E:\\Temp\\images\\word\\";
        File dest = new File(filePath + fileName);
        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            //不存在就创建一个
            dest.getParentFile().mkdir();
        }
        try {
            file.transferTo(dest);
            String fn[];
            fn=fileName.split("\\.");
            tessimg(dest,fn[0]);
            return "成功，文件地址为："+"E:\\Temp\\txts\\"+fileName;
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        }
        return "上传失败！";
    }

    @PostMapping("/upload2")
    @ResponseBody
    public String upload2(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        String fileName = file.getOriginalFilename();
        String filePath = "E:\\Temp\\images\\pdf\\";
        File dest = new File(filePath + fileName );

        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            //不存在就创建一个
            dest.getParentFile().mkdir();
        }

        try {

            PDDocument doc = PDDocument.load(multipartFileToFile(file));
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                String fn[];
                fn=fileName.split("\\.");
                File my=new File(filePath + "\\" + fn + "_" + (i + 1) + "." + "jpg");
                ImageIO.write(image, "jpg", my);
                tesspdf(my,i+1);
            }
            return "成功,文件地址为："+"E:\\Temp\\txts\\"+fileName;
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "上传成功";
    }



        public static void pdf2png(String fileAddress,String filename,String type) {
        // 将pdf装图片 并且自定义图片得格式大小
        File file = new File(fileAddress+"\\"+filename+".pdf");
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                ImageIO.write(image, type, new File(fileAddress+"\\"+filename+"_"+(i+1)+"."+type));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *自由确定起始页和终止页
     * @param fileAddress 文件地址
     * @param filename pdf文件名
     * @param indexOfStart 开始页  开始转换的页码，从0开始
     * @param indexOfEnd 结束页  停止转换的页码，-1为全部
     * @param type 图片类型
     */
    public static void pdf2png(String fileAddress,String filename,int indexOfStart,int indexOfEnd,String type) {
        // 将pdf装图片 并且自定义图片得格式大小
        File file = new File(fileAddress+"\\"+filename+".pdf");
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = indexOfStart; i < indexOfEnd; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                File myfile=new File(fileAddress+"\\"+filename+"_"+(i+1)+"."+type);
                ImageIO.write(image, type, myfile);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除本地临时文件
     * @param file
     */
    public static void delteTempFile(File file) {
        if (file != null) {
            File del = new File(file.toURI());
            del.delete();
        }
    }


    public static void tesspdf(File imageFile,int i) throws IOException {
        //图片存储地址
        if(!imageFile.exists()){
            System.out.println("图片不存在");;
        }
        Tesseract tessreact = new Tesseract();
        tessreact.setDatapath("E:\\Temp\\tessdata");
        tessreact.setLanguage("chi_sim");

         File txt=new File("E:\\Temp\\txts\\"+i+".txt");
         if (!txt.getParentFile().exists()){
             txt.getParentFile().mkdir();
         }
         if (!txt.exists()){
             txt.createNewFile();
         }

        String result;
        try {
            result = "" + tessreact.doOCR(imageFile);
            PrintWriter pfp= new PrintWriter(txt);
            pfp.print(result);
            pfp.close();
            System.out.println("已成功转换第"+i+"页");
        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void tessimg(File imageFile,String name) throws IOException {
        //图片存储地址
        if(!imageFile.exists()){
            System.out.println("图片不存在");;
        }
        Tesseract tessreact = new Tesseract();
        tessreact.setDatapath("E:\\Temp\\tessdata");
//        tessreact.setLanguage("chi_sim");
        tessreact.setLanguage("eng");
        File txt=new File("E:\\Temp\\txts\\"+name+".txt");
        if (!txt.getParentFile().exists()){
            txt.getParentFile().mkdir();
        }
        if (!txt.exists()){
            txt.createNewFile();
        }

        String result;
        try {
            result = "" + tessreact.doOCR(imageFile);
            PrintWriter pfp= new PrintWriter(txt);
            pfp.print(result);
            pfp.close();
            System.out.println("转换成功");
        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }





}



