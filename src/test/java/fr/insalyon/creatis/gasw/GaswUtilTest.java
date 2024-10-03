package fr.insalyon.creatis.gasw;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GaswUtil tests")
public class GaswUtilTest {
    @Test
    @DisplayName("Uri detection")
    public void uriDetection() {
        assertTrue(GaswUtil.isUri("girder:///control_3DT1.nii"));
        assertTrue(GaswUtil.isUri("ssh://example.com/control_3DT1.nii"));
        assertTrue(GaswUtil.isUri("girder:/control_3DT1.nii"));

        assertFalse(GaswUtil.isUri("girder:control_3DT1.nii"));
        assertFalse(GaswUtil.isUri("girder:////control_3DT1.nii"));
    }

    @Test
    @DisplayName("Script file copy test")
    public void testScriptFileCopy() throws URISyntaxException {
        
            Path sourceScriptFile = Paths.get(getClass().getClassLoader().getResource("script.sh").toURI());
            System.out.println(sourceScriptFile);
    }
}
