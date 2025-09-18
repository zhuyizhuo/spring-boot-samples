package com.zhuo.test.jd;


/**
 * @description:
 * @author: zhuo
 * @date: 2025-09-18 18:45
 */
public class UploadFile {

    public static void main(String[] args) {

    }

//    public void uploadOneFile() throws IOException {
//        String fileName = "111222.xlsx";
//        try {
//            // 获取当前类的类加载器并获取资源的URL
//            // fileName 是文件名，它应该和你的类在同一个包下
//            File excelFile = new File("src/test/java/com/jd/seller/crm/center/main/" + fileName);
//
//            if (excelFile.exists() && !excelFile.isDirectory()) {
//                // 成功获取到File对象，在这里进行你的Excel读取操作
//                System.out.println("文件路径: " + excelFile.getAbsolutePath());
//                String put = jingdongStorageService.bucket(bucketName).object(fileName)
//                        .entity(excelFile.length(),  new FileInputStream(excelFile)).put();
//                String fileUrl = fileManageService.getFileUrl(fileName);
//                System.out.println(fileUrl);
//            } else {
//                System.out.println("文件不存在或是目录: " + excelFile.getAbsolutePath());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
