
package com.hngd.doc.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * test asakds dfjdf fdshfg dfhjdf fdhfjd dfhdjf jfdhjd sdsahjdshjdsf jkdfjdfjd
 * @author
 */
public class AppTest
{
    static
    {
        // register parse type
        new ParamElement();
        new AuthorElement();
        new ReturnElement();
        new SeeElement();
        new SinceElement();
        new ThrowsElement();
    }

    static class ParseResult
    {
        int            startIndex;
        int            endIndex = -1;
        CommentElement elemnet;
    }

    static class CommentElement
    {
        static Map<String, CommentElement> commentElements = new HashMap<>();
        String                             comment;
        String                             prefix;

        /**
         * @param prefix
         * @author
         * @since 0.0.1
         */
        private CommentElement(String prefix)
        {
            this.prefix = prefix;
            if (!commentElements.containsKey(prefix))
            {
                commentElements.put(prefix, this);
            }
        }

        public String parse(String line)
        {
            return line.replace(prefix, "");
        }
    }

    static class DescElement extends CommentElement
    {
        public static final String prefix = "@desc";
        String                     paramName;

        public DescElement()
        {
            super(prefix);
        }

        @Override
        public String parse(String line)
        {
            return line;
        }
    }

    static class ParamElement extends CommentElement
    {
        public static final String prefix = "@param";
        String                     paramName;

        public ParamElement()
        {
            super(prefix);
        }

        @Override
        public String parse(String line)
        {
            String[] items = line.split(" ");
            paramName = items[1];
            return items[2];
        }
    }

    static class ReturnElement extends CommentElement
    {
        public ReturnElement()
        {
            super(prefix);
        }
        public static final String prefix = "@return";
    }

    static class SeeElement extends CommentElement
    {
        public static final String prefix = "@see";

        public SeeElement()
        {
            super(prefix);
        }
    }

    static class AuthorElement extends CommentElement
    {
        public static final String prefix = "@author";

        public AuthorElement()
        {
            super(prefix);
        }
    }

    static class ThrowsElement extends CommentElement
    {
        public static final String prefix = "@throws";

        public ThrowsElement()
        {
            super(prefix);
        }
    }

    static class SinceElement extends CommentElement
    {
        public static final String prefix = "@since";

        public SinceElement()
        {
            super(prefix);
        }
    }

    public static void main(String[] args) throws URISyntaxException, IOException
    {
        URL url = AppTest.class.getResource("comment.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path);
        lines = lines.subList(1, lines.size() - 1);
        String desc = "";
        List<CommentElement> pce = new ArrayList<>();
        ParseResult pr = parseDesc(lines);
        desc = pr.elemnet.comment;
        pce.add(pr.elemnet);
        while (pr.endIndex < lines.size())
        {
            pr = parseElement(lines, pr.endIndex);
            pce.add(pr.elemnet);
        }
        pce.forEach(ce ->
        {
            System.out.println(ce.prefix + "-->" + ce.comment);
        });
    }

    private static ParseResult parseDesc(List<String> lines)
    {
        ParseResult pr = new ParseResult();
        pr.startIndex = 0;
        StringBuilder desc = new StringBuilder();
        for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);
            line = line.replaceFirst("\\*", "").trim();
            if (line.startsWith("@"))
            {
                pr.endIndex = i;
                break;
            }
            desc.append(line).append(" ");
        }
        CommentElement ce = new DescElement();
        ce.comment = desc.toString();
        pr.elemnet = ce;
        return pr;
    }

    private static ParseResult parseElement(List<String> lines, int startIndex)
    {
        CommentElement ce = null;
        ParseResult pr = new ParseResult();
        pr.startIndex = startIndex;
        StringBuilder desc = new StringBuilder();
        boolean isFountAt = false;
        for (int i = startIndex; i < lines.size(); i++)
        {
            if (StringUtils.isEmpty(lines.get(i)))
            {
                continue;
            }
            String line = lines.get(i).replaceFirst("\\*", "").trim();
            if (line.startsWith("@"))
            {
                if (isFountAt)
                {
                    pr.endIndex = i;
                    break;
                } else
                {
                    Set<String> keys = CommentElement.commentElements.keySet();
                    for (String key : keys)
                    {
                        if (line.startsWith(key))
                        {
                            CommentElement ceType = CommentElement.commentElements.get(key);
                            try
                            {
                                ce = ceType.getClass().newInstance();
                            } catch (InstantiationException | IllegalAccessException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            line = ce.parse(line);
                        }
                    }
                    if (ce == null)
                    {
                        throw new RuntimeException("could not parse line[" + line + "]");
                    }
                }
            }
            desc.append(line).append(" ");
        }
        if (pr.endIndex == -1)
        {
            pr.endIndex = lines.size();
        }
        pr.elemnet = ce;
        ce.comment = desc.toString();
        return pr;
    }
}
