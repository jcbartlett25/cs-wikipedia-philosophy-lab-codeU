package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
    // WikiFetcher singleton
	final static WikiFetcher fetcher = new WikiFetcher();
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
        ArrayList<String> visited = new ArrayList<String>();

		String start = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        String finish = "https://en.wikipedia.org/wiki/Philosophy";
        String url = start;

        for (int i = 0; i < 15; i++) {

            if (visited.contains(url)) {
                return;
            }
            else {
                visited.add(url); 
            }

            Elements paragraphs = fetcher.fetchWikipedia(url);
            Element link = searchPage(paragraphs);

            if (link == null) {
                System.err.println("No links on this page...");
                return;
            }

            url = link.attr("abs:href");
            System.out.println("currently on: " + url);
            
            if (url.equals(finish)) {
                System.out.println("found it!");
                break;
            }
        }
	}

    public static Element searchPage(Elements paragraphs) {

        for (Element paragraph : paragraphs) {

            Element firstLink = findLinkInParagraph(paragraph);

            if (firstLink != null) {
                return firstLink;
            }
        }

        // No links in the page
        return null;
    }

    private static Element findLinkInParagraph(Element paragraph) {

        Iterable<Node> nodes = new WikiNodeIterable((Node) paragraph);

        for (Node node : nodes) {

            if(node instanceof TextNode)
                continue;

            else {

                Element firstLink = validateLink((Element) node);

                if (firstLink != null) {
                    return firstLink;
                }
            }
        }

        // No links in the paragraph
        return null;
    }

    private static Element validateLink(Element elt) {

        boolean isValid = true;

        //******************************
        if (!elt.tagName().equals("a")) 
            isValid = false;
        
        if (isItalic(elt)) 
            isValid = false;
        
        if (elt.attr("href").startsWith("#")) 
            isValid = false;
        
        if (elt.attr("href").startsWith("/wiki/Help:")) 
            isValid = false;
        //*********************************

        if (isValid) 
            return elt;

        else 
            return null;     
    }

    // Couldn't figure this one out, so I used the solution as a reference
    private static boolean isItalic(Element elt) {

        while (elt != null) {

            if (elt.tagName().equals("i") || elt.tagName().equals("em"))
                return true;

            elt = elt.parent();
        }
        return false;
    }
}
