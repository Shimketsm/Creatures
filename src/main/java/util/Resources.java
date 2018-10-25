package util;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;

public final class Resources {

    public static final class ResourceType {

        public static final ResourceType IMAGE = new ResourceType("png");
        public static final ResourceType SOUND = new ResourceType("wav");
        public static final ResourceType TEXT = new ResourceType("txt");
        public static final ResourceType DATA = new ResourceType("dat");

        protected final String post;

        private ResourceType(String postfix) {
            post = postfix;
        }

        public String getFileName(String name) {
            return name + "." + post;
        }

        public static ResourceType getType(String post) {
            return new ResourceType(post);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ResourceType && post.equals(((ResourceType) o).post);
        }

        @Override
        public int hashCode() {
            //offset hashCode so this isn't equal to post's hashcode.
            return 97 + post.hashCode();
        }

    };

    //generic practically useless here because of freaking java's type erasure implementation.
    // just use type types, for gods sake!
    public static final class Resource {

        final ResourceType type;

        final String name;

        public Resource(String name, ResourceType type) {
            this.name = name;
            this.type = type;
        }

        public Object getResource() {
            return RESOURCES.get(this);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Resource && name.equalsIgnoreCase(((Resource) o).name) && type.equals(((Resource) o).type);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 83 * hash + type.hashCode();
            hash = 83 * hash + name.hashCode();
            return hash;
        }
    }

    public static final File RES_DIR = new File("resources");

    //maps a Resource key to some object data, an image, String, audio Clip, or some data object
    private static final HashMap<Resource, Object> RESOURCES = new HashMap<>();

    static {
        init();
    }

    public static final void init() {

        load(RES_DIR);

//        System.out.print("[");
//        for(Resource o : resources.keySet()) {
//            System.out.print(o.name + "=" + o.type.post + " ");
//        }
//
//        System.out.print("]");

        //load bundled String resources
        String[] lines = getText("resources").split(System.lineSeparator());
        for (String name : lines) {
            //System.out.println("processing " + "\'" + name + "\'");
            if (name.startsWith("#")) {
                continue;
            }

            String[] n = name.split("=");

            System.out.println("Loading " + n[0].trim() + " with value " + n[1].trim());

            RESOURCES.put(new Resource(n[0].trim().toLowerCase(), ResourceType.TEXT), n[1].trim());
        }

    }

    //must include directory prefixes
    public static final void load(String s) {
        load(new File(RES_DIR, s));
    }

    //loads a file or recursively loads a directory.
    public static final void load(File f) {

        if (f.isFile() && f.canRead() && !f.isHidden()) {
            if (f.getName().endsWith("wav")) return;
            System.out.println("Loading " + f.getAbsolutePath());
            //try reading file as image
            try {
                BufferedImage img = ImageIO.read(f);
                //System.out.println("Loading " + f.getAbsolutePath() + " as image = " + img!=null);
                if (img != null) {
                    RESOURCES.put(new Resource(f.getName().split("\\.")[0].toLowerCase(), ResourceType.IMAGE), img);
                    return;
                }
            } catch (IOException ex) {
                throw new Error("Can't decode file " + f.getAbsolutePath());
            }

//            //try reading file as sound
//            try {
//
//
//            } catch (IOException ex) {
//                throw new Error("Can't decode file " + f.getAbsolutePath());
//            }
//
//            //try reading file as data
//            try {
//
//
//            } catch (IOException ex) {
//                throw new Error("Can't decode file " + f.getAbsolutePath());
//            }
            //try reading file as text
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                RESOURCES.put(new Resource(f.getName().substring(0, f.getName().lastIndexOf(".")).toLowerCase(), ResourceType.TEXT), sb.toString());
                //return;
            } catch (IOException ex) {
                throw new Error("Can't decode file " + f.getAbsolutePath());
            }

        } else if (f.isDirectory()) {
            for (File fi : f.listFiles()) {
                load(fi);
            }
        }
    }

//I wanted to do one single get that would return the correct type,
//but because of the way java works, we have four, instead of any possibility.
//  Come on Java!
    /**
     *
     * @param s name of the image to get
     * @return a BufferedImage, or null
     */
    public static final BufferedImage getImage(String s) {
        BufferedImage r = (BufferedImage) new Resource(s.toLowerCase(), ResourceType.IMAGE).getResource();
        if (r == null) {
            throw new Error("Could not find image resource " + s);
        }
        return r;
    }

    public static final Clip getSound(String s) {
        Clip r = (Clip) new Resource(s.toLowerCase(), ResourceType.SOUND).getResource();
        if (r == null) {
            throw new Error("Could not find sound resource " + s);
        }
        return r;
    }

    public static final Object getData(String s) {
        Object r = new Resource(s.toLowerCase(), ResourceType.DATA).getResource();
        if (r == null) {
            throw new Error("Could not find data resource " + s);
        }
        return r;
    }

    /**
     * Calls and then returns toString() on whatever is mapped to the passed in name, as long as it is labeled as a text resource.
     *
     * @param s
     * @return
     */
    public static final String getText(String s) {
        Object r = new Resource(s.toLowerCase(), ResourceType.TEXT).getResource();
        if (r == null) {
            throw new Error("Could not find text resource " + s);
        }
        return r.toString();
    }
}
