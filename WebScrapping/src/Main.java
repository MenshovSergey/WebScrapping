import com.sun.org.apache.xerces.internal.util.URI;

import javax.swing.text.*;
import javax.swing.text.html.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    private static HashMap<String, Integer> count = new HashMap<String, Integer>();
    private static HashMap<String, ArrayList<String>> extract = new HashMap<String, ArrayList<String>>();
    private static boolean w, e, c, v;
    public static void main(String[] args) {

        ArrayList<String> urls = new ArrayList<String>();
        urls.add(args[0]);
        if ((urls = getUrls(urls.get(0))) == null) {
            urls = new ArrayList<String>();
            urls.add(args[0]);
        }
        String[] words = args[1].split(",");
        for (int i = 0; i < words.length; i++) {
            count.put(words[i], 0);
        }
        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("-v")) {
                v = true;
            }
            if (args[i].equals("-c")) {
                c = true;
            }
            if (args[i].equals("-w")) {
                w = true;
            }
            if (args[i].equals("-e")) {
                e = true;
            }
        }


        for (String url:urls) {
            long start = System.currentTimeMillis();
            parseWeb(url);
            System.out.println("time on " + url +" = " +(System.currentTimeMillis() - start) );
        }
    }

    private static ArrayList<String> getUrls(String s) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(s));
            String cur = "";
            while ((cur = bf.readLine()) != null) {
                result.add(cur);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private static void parseWeb(String name) {
        try {
            for (String i : count.keySet()) {
                count.put(i, 0);
                extract.put(i, new ArrayList<String>());
            }
            URL url = new URL(name);
            HTMLEditorKit kit = new HTMLEditorKit();

            HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
            doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
            Reader HTMLReader = new InputStreamReader(url.openConnection().getInputStream());
            kit.read(HTMLReader, doc, 0);


            long ans = 0;
            ElementIterator it = new ElementIterator(doc);
            Element elem;

            while( (elem = it.next()) != null) {
                if ("body".equals(elem.getName())) {
                    ans += elem.getEndOffset() - elem.getStartOffset();
                    String cur = doc.getText(elem.getStartOffset(), elem.getEndOffset() - elem.getStartOffset());
                    for (String i : count.keySet()) {
                        Answer all = getAll(i, cur);
                        if (all.sum > 0) {
                            if (e) {
                                ArrayList<String> t = extract.get(i);
                                t.addAll(all.ans);
                                extract.put(i, t);
                            }
                            int k = count.get(i);
                            k += all.sum;
                            count.put(i, k);
                        }
                    }
                    //System.out.println();
                }

            }
            //System.out.println(name +" count symbol = " + ans);
            System.out.println(name +" count characters " + ans);
            for (String i : count.keySet()) {
                int sum = count.get(i);
                System.out.println(i +" " + sum);
                if (e && sum > 0) {
                    for (String j : extract.get(i)) {
                        System.out.println("  " + j);
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static Answer getAll(String s, String t) {
        int res = 0;
        ArrayList<String> occurrences = new ArrayList<>();
        for (int i = 0; i < t.length() - s.length(); i++) {
            if (t.substring(i, i + s.length()).equals(s)) {
                occurrences.add(t.substring(Math.max(i - 10, 0), Math.min(i + s.length() + 10, t.length())));
                res++;
            }
        }
        return new Answer(res, occurrences);
    }
    private static class Answer {
        int sum = 0;
        ArrayList<String> ans = new ArrayList<>();
        public Answer(int sum, ArrayList<String> ans) {
            this.sum = sum;
            this.ans = ans;
        }
    }
}
