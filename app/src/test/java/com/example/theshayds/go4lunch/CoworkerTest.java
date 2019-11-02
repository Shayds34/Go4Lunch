package com.example.theshayds.go4lunch;

import com.example.theshayds.go4lunch.models.Coworker;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CoworkerTest {

    @Test
    public void testSetAndGet(){
         String uid = "fze5451fz";
         String userName = "Mark Ruffalo";
         String urlPicture = "https://upload.wikimedia.org/wikipedia/commons/8/8a/Mark_Ruffalo_in_2017_by_Gage_Skidmore.jpg";
         boolean hasChosen = true;
         String placeID = "ChIJZ1yguXClthIRDA3HlOPbtzA";
         String placeName = "Au Bureau Montpellier";

        Coworker coworker = new Coworker();
        coworker.setUid(uid);
        coworker.setUserName(userName);
        coworker.setUrlPicture(urlPicture);
        coworker.setHasChosen(hasChosen);
        coworker.setPlaceID(placeID);
        coworker.setPlaceName(placeName);

        String expectedUid = coworker.getUid();
        String expectedUserName = coworker.getUserName();
        String expectedUrlPicture = coworker.getUrlPicture();
        boolean expectedHasChosen = coworker.getHasChosen();
        String expectedPlaceID = coworker.getPlaceID();
        String expectedPlaceName = coworker.getPlaceName();

        assertEquals(expectedUid, uid);
        assertEquals(expectedUserName, userName);
        assertEquals(expectedUrlPicture, urlPicture);
        assertEquals(expectedHasChosen, hasChosen);
        assertEquals(expectedPlaceID, placeID);
        assertEquals(expectedPlaceName, placeName);
    }

}
