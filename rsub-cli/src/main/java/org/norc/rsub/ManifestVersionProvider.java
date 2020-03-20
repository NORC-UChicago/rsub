package org.norc.rsub;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import picocli.CommandLine.IVersionProvider;

public class ManifestVersionProvider implements IVersionProvider {
  @Override
  public String[] getVersion() throws Exception {
    Enumeration<URL> resources = App.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
    while (resources.hasMoreElements()) {
      URL url = resources.nextElement();
      try {
        Manifest manifest = new Manifest(url.openStream());
        if (isApplicableManifest(manifest)) {
          Attributes attributes = manifest.getMainAttributes();
          return new String[] {
              attributes.get(key("Implementation-Title")) + " version \"" + attributes.get(key("Implementation-Version")) + "\""
          };
        }
      } catch (IOException ex) {
        return new String[] { "Unable to read from " + url + ": " + ex };
      }
    }
    return new String[0];
  }

  private boolean isApplicableManifest(Manifest manifest) {
    Attributes attributes = manifest.getMainAttributes();
    return "rsub-cli".equals(attributes.get(key("Implementation-Title")));
  }

  private static Attributes.Name key(String key) {
    return new Attributes.Name(key);
  }
}
