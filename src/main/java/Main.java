import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Main {
    private static final Integer NOMINAL_PLATE_WIDTH = 29;
    private static final Integer NOMINAL_PLATE_HEIGHT = 22;

    private static final Integer SUIT_PLATE_WIDTH = 30;
    private static final Integer SUIT_PLATE_HEIGHT = 33;

    private static final String ETALON_NOMINALS_PATH = "./etalon/nominals";
    private static final String SUIT_NOMINALS_PATH = "./etalon/suits";

    public static void main(String[] args) throws IOException {
        if (args == null || args.length < 1) {
            System.out.println("Вы не указали путь до папки с данными");
            return;
        }

        Path etalonNominalsPath = Paths.get(ETALON_NOMINALS_PATH);
        Map<String, Boolean[][]> etalonNominals = getEtalon(etalonNominalsPath, NOMINAL_PLATE_WIDTH, NOMINAL_PLATE_HEIGHT);

        Path etalonSuitsPath = Paths.get(SUIT_NOMINALS_PATH);
        Map<String, Boolean[][]> etalonSuits = getEtalon(etalonSuitsPath, SUIT_PLATE_WIDTH, SUIT_PLATE_HEIGHT);

        Path root = Paths.get(args[0]);
        Files.walk(root, 1).filter(el -> !el.normalize().endsWith(root.normalize())).forEach(el -> {
                    String actual = "";
                    try {
                        BufferedImage img = ImageIO.read(el.toFile());
                        BufferedImage nominalPlate1 = img.getSubimage(148, 592, NOMINAL_PLATE_WIDTH, NOMINAL_PLATE_HEIGHT);
                        BufferedImage nominalPlate2 = img.getSubimage(220, 592, NOMINAL_PLATE_WIDTH, NOMINAL_PLATE_HEIGHT);
                        BufferedImage nominalPlate3 = img.getSubimage(292, 592, NOMINAL_PLATE_WIDTH, NOMINAL_PLATE_HEIGHT);
                        BufferedImage nominalPlate4 = img.getSubimage(363, 592, NOMINAL_PLATE_WIDTH, NOMINAL_PLATE_HEIGHT);
                        BufferedImage nominalPlate5 = img.getSubimage(435, 592, NOMINAL_PLATE_WIDTH, NOMINAL_PLATE_HEIGHT);

                        BufferedImage suitPlate1 = img.getSubimage(170, 634, SUIT_PLATE_WIDTH, SUIT_PLATE_HEIGHT);
                        BufferedImage suitPlate2 = img.getSubimage(241, 634, SUIT_PLATE_WIDTH, SUIT_PLATE_HEIGHT);
                        BufferedImage suitPlate3 = img.getSubimage(313, 634, SUIT_PLATE_WIDTH, SUIT_PLATE_HEIGHT);
                        BufferedImage suitPlate4 = img.getSubimage(385, 634, SUIT_PLATE_WIDTH, SUIT_PLATE_HEIGHT);
                        BufferedImage suitPlate5 = img.getSubimage(456, 634, SUIT_PLATE_WIDTH, SUIT_PLATE_HEIGHT);

                        String nominal = parse(nominalPlate1, etalonNominals, NOMINAL_PLATE_WIDTH, NOMINAL_PLATE_HEIGHT, true);
                        actual = actual + nominal;
                        String suit = parse(suitPlate1, etalonSuits, SUIT_PLATE_WIDTH, SUIT_PLATE_HEIGHT, false);
                        actual = actual + suit;

                        nominal = parse(nominalPlate2, etalonNominals, NOMINAL_PLATE_WIDTH, NOMINAL_PLATE_HEIGHT, true);
                        actual = actual + nominal;
                        suit = parse(suitPlate2, etalonSuits, SUIT_PLATE_WIDTH, SUIT_PLATE_HEIGHT, false);
                        actual = actual + suit;

                        nominal = parse(nominalPlate3, etalonNominals, NOMINAL_PLATE_WIDTH, NOMINAL_PLATE_HEIGHT, true);
                        actual = actual + nominal;
                        suit = parse(suitPlate3, etalonSuits, SUIT_PLATE_WIDTH, SUIT_PLATE_HEIGHT, false);
                        actual = actual + suit;

                        nominal = parse(nominalPlate4, etalonNominals, NOMINAL_PLATE_WIDTH, NOMINAL_PLATE_HEIGHT, true);
                        actual = actual + nominal;
                        suit = parse(suitPlate4, etalonSuits, SUIT_PLATE_WIDTH, SUIT_PLATE_HEIGHT, false);
                        actual = actual + suit;

                        nominal = parse(nominalPlate5, etalonNominals, NOMINAL_PLATE_WIDTH, NOMINAL_PLATE_HEIGHT, true);
                        actual = actual + nominal;
                        suit = parse(suitPlate5, etalonSuits, SUIT_PLATE_WIDTH, SUIT_PLATE_HEIGHT, false);
                        actual = actual + suit;

                        System.out.println(el.toFile().getName() + " - " + actual.trim());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

    }

    private static Boolean isBlack(Integer color) {
        int r = (color & 0xFF << 16) >> 16;
        int g = (color & 0xFF << 8) >> 8;
        int b = color & 0xFF;
        // grey background
        if (r == 120 && g == 120 && b == 120) {
            return false;
            // bright red
        } else if ((r > 185 && g > 53 && g < 93 && b > 53 && b < 93)) {
            return true;
            // dart red
        } else if ((r > 77 && r < 117 && g > 16 && g < 56 && b > 16 && b < 56)) {
            return true;
            // grey
        } else return r < 65 && g < 65 && b < 65;
    }

    private static boolean isEmpty(BufferedImage inImg) {
        int width = inImg.getWidth();
        int high = inImg.getHeight();
        boolean isBlack = true;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < high; j++) {
                isBlack = isBlack & isBlack(inImg.getRGB(i,j));
            }
        }
        return isBlack;
    }

    private static String parse(BufferedImage plate, Map<String, Boolean[][]> etalons, int w, int h, boolean isUpperCase) {
        if (isEmpty(plate)) {
            return "";
        }
        float max = -1;
        String key = "";
        for (Map.Entry<String, Boolean[][]> etalonMatrix : etalons.entrySet()) {
            float res = calculatePercentageOfMatching(plate, etalonMatrix.getValue(), w, h);
            if (res > max) {
                max = res;
                key = etalonMatrix.getKey();
            }
        }
        return isUpperCase ? key.toUpperCase(Locale.ROOT) : key.toLowerCase(Locale.ROOT);
    }

    private static float calculatePercentageOfMatching(BufferedImage inImg, Boolean[][] e, int w, int h) {
        int width = inImg.getWidth();
        int high = inImg.getHeight();
        Boolean[][] current = new Boolean[w][h];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < high; j++) {
                int color = inImg.getRGB(i, j);
                current[i][j] = isBlack(color);
            }
        }

        int count = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < high; j++) {
                if (current[i][j] == e[i][j]) {
                    count++;
                }
            }
        }

        return (float) (count / ((w * h) / 100.0));
    }

    private static Map<String, Boolean[][]> getEtalon(Path etalonPath, int w, int h) throws IOException {
        Map<String, Boolean[][]> etalons = new HashMap<>();
        Files.walk(etalonPath, 1).filter(el -> !el.normalize().endsWith(etalonPath.normalize())).forEach(el -> {
            try {
                Boolean[][] e = new Boolean[w][h];
                BufferedImage img = ImageIO.read(el.toFile());
                for (int i = 0; i < w; i++) {
                    for (int j = 0; j < h; j++) {
                        int color = img.getRGB(i, j);
                        e[i][j] = isBlack(color);
                    }
                }
                String fileName = el.toFile().getName();
                String key = fileName.substring(0, fileName.indexOf("."));

                etalons.put(key, e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return etalons;
    }

}
