package footprints.javancss;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OutputParser
{
  public Collection<MethodEntry> parse( final File inputFile )
    throws Exception
  {
    final Element element = loadNormalizedDocument( inputFile );
    final XPath xPath = XPathFactory.newInstance().newXPath();
    final HashSet<String> packages = extractPackageNames( element, xPath );

    return extractMethodStatistics( element, packages, xPath );
  }

  private LinkedList<MethodEntry> extractMethodStatistics( final Element element,
                                                           final HashSet<String> packages,
                                                           final XPath xPath )
    throws XPathExpressionException
  {
    final LinkedList<MethodEntry> methods = new LinkedList<MethodEntry>();
    final NodeList methodElements =
      (NodeList) xPath.compile( "//functions/function" ).evaluate( element, XPathConstants.NODESET );
    final int length = methodElements.getLength();
    for ( int i = 0; i < length; i++ )
    {
      final Element methodElement = (Element) methodElements.item( i );
      methods.add( toMethodSpec( methodElement, packages ) );
    }
    return methods;
  }

  private MethodEntry toMethodSpec( final Element element, final HashSet<String> packages )
    throws XPathExpressionException
  {
    final String name = extractString( element, "name" );

    final int methodStart = name.lastIndexOf( '.' );
    final String methodName = name.substring( methodStart + 1 );

    String packageName = "";
    String className = name.substring( 0, methodStart );
    int index = methodStart;
    while ( -1 != ( index = name.lastIndexOf( '.', index - 1 ) ) )
    {
      className = name.substring( index + 1, methodStart );
      packageName = name.substring( 0, index );
      if( packages.contains( packageName ) )
      {
        break;
      }
    }

    final Integer ncss = extractInteger( element, "ncss" );
    final Integer ccn = extractInteger( element, "ccn" );
    final Integer jvdc = extractInteger( element, "javadocs" );
    return new MethodEntry( packageName, className, methodName, ncss, ccn, jvdc );
  }

  private Integer extractInteger( final Element element, final String childElementName )
  {
    return Integer.decode( extractString( element, childElementName ) );
  }

  private String extractString( final Element element, final String childElementName )
  {
    final NodeList nodes = element.getElementsByTagName( childElementName );
    if ( 1 != nodes.getLength() )
    {
      throw new IllegalStateException(
        "Unexpected number of child elements looking for " + childElementName + " in " + element );
    }
    final Node node = nodes.item( 0 );
    if ( node.getNodeType() != Node.ELEMENT_NODE )
    {
      throw new IllegalStateException( "Unexpected node type for " + node );
    }
    return node.getTextContent();
  }

  private HashSet<String> extractPackageNames( final Element element, final XPath xPath )
    throws XPathExpressionException
  {
    final HashSet<String> packages = new HashSet<String>();

    final NodeList packageNames =
      (NodeList) xPath.compile( "//packages/package/name" ).evaluate( element, XPathConstants.NODESET );
    final int length = packageNames.getLength();
    for ( int i = 0; i < length; i++ )
    {
      final Element node = (Element) packageNames.item( i );
      final String content = node.getTextContent();
      if ( !".".equals( content ) )
      {
        packages.add( content );
      }
    }
    return packages;
  }

  private Element loadNormalizedDocument( final File inputFile )
    throws ParserConfigurationException, SAXException, IOException
  {
    final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    final Document doc = db.parse( inputFile );
    final Element element = doc.getDocumentElement();
    element.normalize();
    return element;
  }

}
