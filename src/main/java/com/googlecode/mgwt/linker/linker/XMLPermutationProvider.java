package com.googlecode.mgwt.linker.linker;

import com.googlecode.mgwt.linker.server.BindingProperty;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XMLPermutationProvider
  implements Serializable
{
  private static final Logger LOG = Logger.getLogger( XMLPermutationProvider.class.getName() );

  private static final String PERMUTATION_NODE = "permutation";
  private static final String PERMUTATION_NAME = "name";
  private static final String PERMUTATIONS = "permutations";

  private XMLPermutationProvider()
  {
  }

  public static Map<String, List<BindingProperty>> getBindingProperties( final InputStream stream )
    throws Exception
  {
    final Map<String, List<BindingProperty>> map = new HashMap<String, List<BindingProperty>>();

    final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( stream );
    final Element permutationsNode = document.getDocumentElement();
    final String tagName = permutationsNode.getTagName();
    if ( !PERMUTATIONS.equals( tagName ) )
    {
      LOG.severe( "unexpected xml structure: Expected node : '" + PERMUTATIONS + "' got: '" + tagName + "'" );
      throw new Exception();
    }

    final NodeList permutationsChildren = permutationsNode.getChildNodes();

    final int length = permutationsChildren.getLength();
    for ( int i = 0; i < length; i++ )
    {
      final Node node = permutationsChildren.item( i );
      if ( node.getNodeType() != Node.ELEMENT_NODE )
      {
        continue;
      }
      final Element permutationNode = (Element) node;
      handlePermutation( map, permutationNode );
    }
    return map;
  }

  protected static void handlePermutation( final Map<String, List<BindingProperty>> map, Element permutationNode )
    throws Exception
  {
    final String strongName = permutationNode.getAttribute( PERMUTATION_NAME );

    final ArrayList<BindingProperty> list = new ArrayList<BindingProperty>();
    map.put( strongName, list );

    final NodeList variableNodes = permutationNode.getChildNodes();
    for ( int i = 0; i < variableNodes.getLength(); i++ )
    {
      final Node item = variableNodes.item( i );
      if ( Node.ELEMENT_NODE != item.getNodeType() )
      {
        continue;
      }
      final Element variables = (Element) item;
      final String varKey = variables.getTagName();
      final NodeList childNodes = variables.getChildNodes();
      if ( childNodes.getLength() != 1 )
      {
        LOG.severe( "Unexpected XML Structure: Expected property value" );
        throw new Exception( "Unexpected XML Structure: Expected property value" );
      }

      final String varValue = childNodes.item( 0 ).getNodeValue();
      list.add( new BindingProperty( varKey, varValue ) );
    }
  }

  public static String serializeMap( final Map<String, Set<BindingProperty>> map )
    throws Exception
  {
    final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    final Element permutationsNode = document.createElement( PERMUTATIONS );
    document.appendChild( permutationsNode );

    for ( final Entry<String, Set<BindingProperty>> entry : map.entrySet() )
    {
      final Element node = document.createElement( PERMUTATION_NODE );
      node.setAttribute( PERMUTATION_NAME, entry.getKey() );
      permutationsNode.appendChild( node );

      for ( final BindingProperty b : entry.getValue() )
      {
        final Element variable = document.createElement( b.getName() );
        variable.appendChild( document.createTextNode( b.getValue() ) );
        node.appendChild( variable );
      }
    }
    return transformDocumentToString( document );
  }

  protected static String transformDocumentToString( final Document document )
    throws Exception
  {
    final StringWriter xml = new StringWriter();
    final Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
    transformer.transform( new DOMSource( document ), new StreamResult( xml ) );
    return xml.toString();
  }

  public static String writePermutationInformation( final String strongName,
                                             final Set<BindingProperty> bindingProperties,
                                             final Set<String> files )
    throws Exception
  {
    final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    final Element permutationNode = document.createElement( PERMUTATION_NODE );
    document.appendChild( permutationNode );

    permutationNode.setAttribute( PERMUTATION_NAME, strongName );

    // create and append variables node
    final Element variablesNode = document.createElement( "variables" );
    permutationNode.appendChild( variablesNode );

    // write out all variables
    for ( final BindingProperty prop : bindingProperties )
    {
      final Element varNode = document.createElement( prop.getName() );
      varNode.appendChild( document.createTextNode( prop.getValue() ) );
      variablesNode.appendChild( varNode );
    }

    // create file node
    final Element filesNode = document.createElement( "files" );
    permutationNode.appendChild( filesNode );

    // write out all files
    for ( final String string : files )
    {
      final Element fileNode = document.createElement( "file" );
      fileNode.appendChild( document.createTextNode( string ) );
      filesNode.appendChild( fileNode );
    }

    return transformDocumentToString( document );
  }
}
