import com.tac.guns.graph.Texture;
import com.tac.guns.graph.util.Buffers;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.IOUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;


import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.stb.STBImage.*;

public class Test {
    public static void main(String[] args) {
        /*ResourceLocation modelResource = new ResourceLocation("tac", "models/test.glb");
        System.out.print(modelResource.getPath().split("\\.")[1]);
         */
        String str = "wwww1";
        if (str.matches(".*[0-9].*")){
            String number = Pattern.compile("[^0-9]").matcher(str).replaceAll("").trim();
            int i=Integer.parseInt(number);
            System.out.print(i);
        }


    }
}
