package comlisanshuxue.lisan.ceshiyong;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class VennDiagram {
    public static void main(String[] args) {
        // 示例集合
        Set<Integer> setA = new HashSet<>();
        setA.add(1);
        setA.add(2);
        setA.add(3);

        Set<Integer> setB = new HashSet<>();
        setB.add(2);
        setB.add(3);
        setB.add(4);

        // 计算并集
        Set<Integer> union = new HashSet<>(setA);
        union.addAll(setB);

        // 创建并集的图片
        createVennDiagram(union);
    }

    private static void createVennDiagram(Set<Integer> union) {
        int width = 200, height = 200;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 背景设为白色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // 画集合的并集
        g2d.setColor(Color.BLUE);
        g2d.drawString("Union: {" + union.toString().replaceAll("[\\[\\], ]", "") + "}", 50, 100);

        // 释放资源
        g2d.dispose();

        // 保存图片
        try {
            ImageIO.write(image, "PNG", new File("UnionDiagram.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
