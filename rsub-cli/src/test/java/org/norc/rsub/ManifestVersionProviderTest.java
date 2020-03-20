package org.norc.rsub;

import static org.junit.Assert.*;

import org.junit.Test;

public class ManifestVersionProviderTest {

  @Test
  public void getVersion() {
    ManifestVersionProvider mvp = new ManifestVersionProvider();
    try {
      String[] version = mvp.getVersion();
      assertEquals("rsub version \"0.1\"", version[0]);
      } catch (Exception err) {
      fail(err.getMessage());
    }
  }
}