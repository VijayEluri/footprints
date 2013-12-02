package com.googlecode.mgwt.linker.linker;

import com.google.gwt.core.ext.Linker;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.Transferable;
import com.googlecode.mgwt.linker.server.BindingProperty;
import java.util.Set;

@Transferable
public class PermutationArtifact
  extends Artifact<PermutationArtifact>
{
  private static final long serialVersionUID = -2097933260935878782L;

  private final Set<String> _permutationFiles;
  private final String _permutationName;
  private final Set<BindingProperty> _bindingProperties;

  public PermutationArtifact( final Class<? extends Linker> linker,
                              final String permutationName,
                              final Set<String> permutationFiles,
                              final Set<BindingProperty> bindingProperties )
  {
    super( linker );
    _permutationName = permutationName;
    _permutationFiles = permutationFiles;
    _bindingProperties = bindingProperties;
  }

  @Override
  public int hashCode()
  {
    return _permutationFiles.hashCode();
  }

  @Override
  protected int compareToComparableArtifact( final PermutationArtifact o )
  {
    return _permutationName.compareTo( o._permutationName );
  }

  @Override
  protected Class<PermutationArtifact> getComparableArtifactType()
  {
    return PermutationArtifact.class;
  }

  public Set<String> getPermutationFiles()
  {
    return _permutationFiles;
  }

  public String getPermutationName()
  {
    return _permutationName;
  }

  public Set<BindingProperty> getBindingProperties()
  {
    return _bindingProperties;
  }
}
