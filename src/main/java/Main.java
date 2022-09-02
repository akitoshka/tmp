import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws IOException {
        Map<String, Boolean[][]> etalons = new HashMap<>();

        Path etalonPath = Paths.get("./etalon");
        Files.walk(etalonPath, 1).filter(el -> !el.normalize().endsWith(etalonPath.normalize())).forEach(el -> {
            try {
                Boolean[][] e = new Boolean[29][22];
                BufferedImage img = ImageIO.read(el.toFile());
                int width = img.getWidth();
                int high = img.getHeight();
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < high; j++) {
                        int color = img.getRGB(i, j);
                        int r = (color & 0xFF << 16) >> 16;
                        int g = (color & 0xFF << 8) >> 8;
                        int b = color & 0xFF;
                        e[i][j] = (r != 255 && g != 255 && b != 255);
                    }
                }
                String fileName =  el.toFile().getName();
                String key = fileName.substring(0,fileName.indexOf("."));
                etalons.put(key, e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        if (args == null || args.length < 1) {
            System.out.println("Вы не указали параметр");
            return;
        }
        Path root = Paths.get(args[0]);
        final AtomicInteger c = new AtomicInteger();
        Files.walk(root, 1).filter(el -> !el.normalize().endsWith(root.normalize())).forEach(el -> {
                    try {
                        System.out.println(el + " номер " + c.get());
                        BufferedImage img = ImageIO.read(el.toFile());
                        BufferedImage subImg1 = img.getSubimage(148, 592, 29, 22);
                        BufferedImage subImg2 = img.getSubimage(220, 592, 29, 22);
                        BufferedImage subImg3 = img.getSubimage(292, 592, 29, 22);
                        BufferedImage subImg4 = img.getSubimage(363, 592, 29, 22);
                        BufferedImage subImg5 = img.getSubimage(432, 592, 29, 22);

//                        System.out.println("1");
                        float max = -1;
                        String key = "";
                        for (Map.Entry<String, Boolean[][]> simbolEtalon : etalons.entrySet()) {
                            float res = check(subImg1,simbolEtalon.getValue());
                            if (res > max){
                                max = res;
                                key = simbolEtalon.getKey();
                            }
//                            System.out.println(el+" 1 "+simbolEtalon.getKey()+" "+res+"%");
                        }
                        System.out.print(key+" ");

//                        System.out.println("2");
                        max = -1;
                        key = "";
                        for (Map.Entry<String, Boolean[][]> simbolEtalon : etalons.entrySet()) {
                            float res = check(subImg2,simbolEtalon.getValue());
                            if (res > max){
                                max = res;
                                key = simbolEtalon.getKey();
                            }
//                            System.out.println(el+" 2 "+simbolEtalon.getKey()+" "+check(subImg2,simbolEtalon.getValue())+"%");
                        }
                        System.out.print(key+" ");

//                        System.out.println("3");
                        max = -1;
                        key = "";
                        for (Map.Entry<String, Boolean[][]> simbolEtalon : etalons.entrySet()) {
                            float res = check(subImg3,simbolEtalon.getValue());
                            if (res > max){
                                max = res;
                                key = simbolEtalon.getKey();
                            }
//                            System.out.println(el+" 3 "+simbolEtalon.getKey()+" "+check(subImg3,simbolEtalon.getValue())+"%");
                        }
                        System.out.print(key+" ");

//                        System.out.println("4");
                        max = -1;
                        key = "";
                        for (Map.Entry<String, Boolean[][]> simbolEtalon : etalons.entrySet()) {
                            float res = check(subImg4,simbolEtalon.getValue());
                            if (res > max){
                                max = res;
                                key = simbolEtalon.getKey();
                            }
//                            System.out.println(el+" 4 "+simbolEtalon.getKey()+" "+check(subImg4,simbolEtalon.getValue())+"%");
                        }
                        System.out.print(key+" ");

//                        System.out.println("5");
                        max = -1;
                        key = "";
                        for (Map.Entry<String, Boolean[][]> simbolEtalon : etalons.entrySet()) {
                            float res = check(subImg5,simbolEtalon.getValue());
                            if (res > max){
                                max = res;
                                key = simbolEtalon.getKey();
                            }
//                            System.out.println(el+" 5 "+simbolEtalon.getKey()+" "+check(subImg5,simbolEtalon.getValue())+"%");
                        }
                        System.out.print(key+"\n\r");

//                        transform(subImg1);
////                        System.out.println("2 " + check(subImg1, e2));
////                        System.out.println("10 " + check(subImg1, e10));
//
//                        transform(subImg2);
////                        System.out.println("2 " + check(subImg2, e2));
////                        System.out.println("10 " + check(subImg2, e10));
//
//                        transform(subImg3);
////                        System.out.println("2 " + check(subImg3, e2));
////                        System.out.println("10 " + check(subImg3, e10));
//
//                        transform(subImg4);
////                        System.out.println("2 " + check(subImg4, e2));
////                        System.out.println("10 " + check(subImg4, e10));
//
//                        transform(subImg5);
////                        System.out.println("2 " + check(subImg5, e2));
////                        System.out.println("10 " + check(subImg5, e10));

                        ImageIO.write(subImg1, "png", new File("c" + c.get() + "-1.png"));
                        ImageIO.write(subImg2, "png", new File("c" + c.get() + "-2.png"));
                        ImageIO.write(subImg3, "png", new File("c" + c.get() + "-3.png"));
                        ImageIO.write(subImg4, "png", new File("c" + c.get() + "-4.png"));
                        ImageIO.write(subImg5, "png", new File("c" + c.get() + "-5.png"));
                        c.incrementAndGet();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

    }

    private static void transform(BufferedImage inImg) {
        int width = inImg.getWidth();
        int high = inImg.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < high; j++) {
                int color = inImg.getRGB(i, j);
                int r = (color & 0xFF << 16) >> 16;
                int g = (color & 0xFF << 8) >> 8;
                int b = color & 0xFF;
                if (r > 100 && r < 140 && g > 100 && g < 140 && b > 100 && b < 140) {
                    inImg.setRGB(i, j, 0xFF | 0xFF << 8 | 0xFF << 16);
                } else if (r > 185 && r < 225 && g > 53 && g < 93 && b > 53 && b < 93) {
                    inImg.setRGB(i, j, 0);
                } else if (r > 77 && r < 117 && g > 16 && g < 56 && b > 16 && b < 56) {
                    inImg.setRGB(i, j, 0);
                } else if (r > 0 && r < 65 && g > 0 && g < 65 && b > 0 && b < 65) {
                    inImg.setRGB(i, j, 0);
                } else {
                    inImg.setRGB(i, j, 0xFF | 0xFF << 8 | 0xFF << 16);
                }
//                if (r<125 && g<125 && b<125){
//                    inImg.setRGB(i,j,0);
//                } else {
//                    inImg.setRGB(i,j,0xFF | 0xFF<<8 | 0xFF<<16);
//                }
//                System.out.println(color +" R="+ ((color & 0xFF<<16)>>16)+" G="+((color & 0xFF<<8)>>8)+" B"+(color & 0xFF));

            }
        }
    }

    private static float check(BufferedImage inImg, Boolean[][] e) {
        int width = inImg.getWidth();
        int high = inImg.getHeight();
        int count = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < high; j++) {
                int color = inImg.getRGB(i, j);
                int r = (color & 0xFF << 16) >> 16;
                int g = (color & 0xFF << 8) >> 8;
                int b = color & 0xFF;
                if ((r > 185 && r < 225 && g > 53 && g < 93 && b > 53 && b < 93) && e[i][j]) {
                    count++;
                } else if ((r > 77 && r < 117 && g > 16 && g < 56 && b > 16 && b < 56) && e[i][j]) {
                    count++;
                } else if (r < 65 && g < 65 && b < 65 && e[i][j]) {
                    count++;
                }
//                if (r<125 && g<125 && b<125){
//                    inImg.setRGB(i,j,0);
//                } else {
//                    inImg.setRGB(i,j,0xFF | 0xFF<<8 | 0xFF<<16);
//                }
//                System.out.println(color +" R="+ ((color & 0xFF<<16)>>16)+" G="+((color & 0xFF<<8)>>8)+" B"+(color & 0xFF));

            }
        }

        float all = 0;
        for (int i = 0; i < e.length; i++) {
            Boolean[] booleans = e[i];
            for (int j = 0; j < booleans.length; j++) {
                boolean aBoolean = booleans[j];
                if (aBoolean) {
                    all++;
                }
            }
        }


        return count / (all / 100);
    }

//    public static Set<String> getAnagramm(String in){
//
//    }
}
