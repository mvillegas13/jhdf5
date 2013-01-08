package test.hdf5lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestH5F {
    private static final String H5_FILE = "test.h5";

    private static final int COUNT_OBJ_FILE = 1;
    private static final int COUNT_OBJ_DATASET = 0;
    private static final int COUNT_OBJ_GROUP = 0;
    private static final int COUNT_OBJ_DATATYPE = 0;
    private static final int COUNT_OBJ_ATTR = 0;
    private static final int COUNT_OBJ_ALL = (COUNT_OBJ_FILE
            + COUNT_OBJ_DATASET + COUNT_OBJ_GROUP + COUNT_OBJ_DATATYPE + COUNT_OBJ_ATTR);
    private static final int[] OBJ_COUNTS = { COUNT_OBJ_FILE,
            COUNT_OBJ_DATASET, COUNT_OBJ_GROUP, COUNT_OBJ_DATATYPE,
            COUNT_OBJ_ATTR, COUNT_OBJ_ALL };
    private static final int[] OBJ_TYPES = { HDF5Constants.H5F_OBJ_FILE,
            HDF5Constants.H5F_OBJ_DATASET, HDF5Constants.H5F_OBJ_GROUP,
            HDF5Constants.H5F_OBJ_DATATYPE, HDF5Constants.H5F_OBJ_ATTR,
            HDF5Constants.H5F_OBJ_ALL };
    int H5fid = -1;

    private final void _deleteFile(String filename) {
        File file = new File(filename);

        if (file.exists()) {
            try {file.delete();} catch (SecurityException e) {}
        }
    }

    @Before
    public void createH5file()
            throws HDF5LibraryException, NullPointerException {
        assertTrue("H5 open ids is 0",H5.getOpenIDCount()==0);

        H5fid = H5.H5Fcreate(H5_FILE, HDF5Constants.H5F_ACC_TRUNC,
                HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        H5.H5Fflush(H5fid, HDF5Constants.H5F_SCOPE_LOCAL);
    }

    @After
    public void deleteH5file() throws HDF5LibraryException {
        if (H5fid > 0) {
            try {H5.H5Fclose(H5fid);} catch (Exception ex) {}
        }
        _deleteFile(H5_FILE);
    }

    @Test
    public void testH5Fget_create_plist() {
        int plist = -1;

        try {
            plist = H5.H5Fget_create_plist(H5fid);
        }
        catch (Throwable err) {
            fail("H5.H5Fget_create_plist: " + err);
        }
        assertTrue(plist > 0);
        try {H5.H5Pclose(plist);} catch (HDF5LibraryException e) {e.printStackTrace();}
    }

    @Test(expected = HDF5LibraryException.class)
    public void testH5Fget_create_plist_closed() throws Throwable {
        int fid = -1;

        try {
            fid = H5.H5Fopen(H5_FILE, HDF5Constants.H5F_ACC_RDWR,
                    HDF5Constants.H5P_DEFAULT);
        }
        catch (Throwable err) {
            fail("H5.H5Fopen: " + err);
        }
        try {
            H5.H5Fclose(fid);
        }
        catch (Exception ex) {
        }

        // it should fail because the file was closed.
        H5.H5Fget_create_plist(fid);
    }

    @Test
    public void testH5Fget_access_plist() {
        int plist = -1;

        try {
            plist = H5.H5Fget_access_plist(H5fid);
        }
        catch (Throwable err) {
            fail("H5.H5Fget_access_plist: " + err);
        }
        assertTrue(plist > 0);
        try {H5.H5Pclose(plist);} catch (HDF5LibraryException e) {e.printStackTrace();}
    }

    @Test(expected = HDF5LibraryException.class)
    public void testH5Fget_access_plist_closed() throws Throwable {
        int fid = -1;

        try {
            fid = H5.H5Fopen(H5_FILE, HDF5Constants.H5F_ACC_RDWR,
                    HDF5Constants.H5P_DEFAULT);
        }
        catch (Throwable err) {
            fail("H5.H5Fopen: " + err);
        }
        try {
            H5.H5Fclose(fid);
        }
        catch (Exception ex) {
        }

        // it should fail because the file was closed.
        H5.H5Fget_access_plist(fid);
    }

    @Test
    public void testH5Fget_intent_rdwr() {
        int intent = 0;
        int fid = -1;

        try {
            fid = H5.H5Fopen(H5_FILE, HDF5Constants.H5F_ACC_RDWR,
                    HDF5Constants.H5P_DEFAULT);
        }
        catch (Throwable err) {
            fail("H5.H5Fopen: " + err);
        }
        try {
            intent = H5.H5Fget_intent(fid);
        }
        catch (Throwable err) {
            fail("H5.H5Fget_intent: " + err);
        }
        assertEquals(intent, HDF5Constants.H5F_ACC_RDWR);

        try {
            H5.H5Fclose(fid);
        }
        catch (Exception ex) {
        }
    }

    @Test
    public void testH5Fget_intent_rdonly() {
        int intent = 0;
        int fid = -1;

        try {
            fid = H5.H5Fopen(H5_FILE, HDF5Constants.H5F_ACC_RDONLY,
                    HDF5Constants.H5P_DEFAULT);
        }
        catch (Throwable err) {
            fail("H5.H5Fopen: " + err);
        }
        try {
            intent = H5.H5Fget_intent(fid);
        }
        catch (Throwable err) {
            fail("H5.H5Fget_intent: " + err);
        }
        assertEquals(intent, HDF5Constants.H5F_ACC_RDONLY);

        try {
            H5.H5Fclose(fid);
        }
        catch (Exception ex) {
        }
    }

    @Test
    public void testH5Fget_obj_count() {
        long count = -1;

        for (int i = 0; i < OBJ_TYPES.length; i++) {
            try {
                count = H5.H5Fget_obj_count_long(H5fid, OBJ_TYPES[i]);
            }
            catch (Throwable err) {
                fail("H5.H5Fget_obj_count: " + err);
            }

            assertEquals(count, OBJ_COUNTS[i]);
        }
    }

    @Test
    public void testH5Fget_obj_ids() {
        long count = 0;
        int max_objs = 100;
        int[] obj_id_list = new int[max_objs];
        int[] open_obj_counts = new int[OBJ_TYPES.length];

        for (int i = 0; i < OBJ_TYPES.length; i++)
            open_obj_counts[i] = 0;

        open_obj_counts[0] = 1;
        for (int i = 0; i < OBJ_TYPES.length - 1; i++)
            open_obj_counts[OBJ_TYPES.length - 1] += open_obj_counts[i];

        for (int i = 0; i < OBJ_TYPES.length; i++) {
            try {
                count = H5.H5Fget_obj_ids_long(H5fid, OBJ_TYPES[i], max_objs,
                        obj_id_list);
            }
            catch (Throwable err) {
                fail("H5.H5Fget_obj_ids: " + err);
            }
            assertEquals(count, open_obj_counts[i]);
        }
    }
}
