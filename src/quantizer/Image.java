package quantizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Image {

    int width = 0;
    int height = 0;


    public int[][] readImage(File file)
    {
        BufferedImage image=null;
        try
        {
            image= ImageIO.read(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        width=image.getWidth();
        height=image.getHeight();
        int[][] pixels=new int[height][width];

        for(int x=0;x<width;x++)
        {
            for(int y=0;y<height;y++)
            {
                int rgb=image.getRGB(x, y);
                int alpha=(rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb >> 0) & 0xff;

                pixels[y][x]=r;
            }
        }

        return pixels;
    }



    public void writeImage(int[][] pixels,File fileOut,int width,int height)
    {
        BufferedImage image2=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB );

        for(int x=0;x<width ;x++)
        {
            for(int y=0;y<height;y++)
            {
                image2.setRGB(x,y,(pixels[y][x]<<16)|(pixels[y][x]<<8)|(pixels[y][x]));
            }
        }
        try
        {
            ImageIO.write(image2, "jpg", fileOut);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
