/*
 * Copyright 2010 ETH Zuerich, CISD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.systemsx.cisd.hdf5;

import ncsa.hdf.hdf5lib.exceptions.HDF5JavaException;

/**
 * An interface that provides methods for reading any one-dimensional data sets as byte arrays. This
 * is particularly useful for opaque data types, which are "black boxes" to the HDF5 library.
 * 
 * @author Bernd Rinn
 */
public interface IHDF5GenericReader
{

    /**
     * Returns the tag of the opaque data type associated with <var>objectPath</var>, or
     * <code>null</code>, if <var>objectPath</var> is not of an opaque data type (i.e. if
     * 
     * <code>reader.getDataSetInformation(objectPath).getTypeInformation().getDataClass() != HDF5DataClass.OPAQUE</code>
     * ).
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @return The tag of the opaque data type, or <code>null</code>.
     */
    public String tryGetOpaqueTag(final String objectPath);

    /**
     * Returns the opaque data type or <code>null</code>, if <var>objectPath</var> is not of such a
     * data type.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @return The opaque data type, or <code>null</code>.
     */
    public HDF5OpaqueType tryGetOpaqueType(final String objectPath);

    /**
     * Reads the data set <var>objectPath</var> as byte array (of rank 1).
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @return The data read from the data set.
     */
    public byte[] readAsByteArray(final String objectPath);

    /**
     * Reads a block from data set <var>objectPath</var> as byte array (of rank 1).
     * <em>Must not be called for data sets of rank other than 1!</em>
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param blockSize The block size in numbers of elements (this will be the length of the
     *            <code>byte[]</code> returned, divided by the size of one element).
     * @param blockNumber The number of the block to read (starting with 0, offset: multiply with
     *            <var>blockSize</var>).
     * @return The data block read from the data set.
     * @throws HDF5JavaException If the data set is not of rank 1.
     */
    public byte[] readAsByteArrayBlock(final String objectPath, final int blockSize,
            final long blockNumber) throws HDF5JavaException;

    /**
     * Reads a block from data set <var>objectPath</var> as byte array (of rank 1).
     * <em>Must not be called for data sets of rank other than 1!</em>
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param blockSize The block size in numbers of elements (this will be the length of the
     *            <code>byte[]</code> returned, divided by the size of one element).
     * @param offset The offset of the block to read as number of elements (starting with 0).
     * @return The data block read from the data set.
     * @throws HDF5JavaException If the data set is not of rank 1.
     */
    public byte[] readAsByteArrayBlockWithOffset(final String objectPath, final int blockSize,
            final long offset) throws HDF5JavaException;

    /**
     * Reads a block from data set <var>objectPath</var> as byte array (of rank 1) into
     * <var>buffer</var>. <em>Must not be called for data sets of rank other than 1!</em>
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param buffer The buffer to read the values in.
     * @param blockSize The block size in numbers of elements (this will be the length of the
     *            <code>byte[]</code> returned, divided by the size of one element).
     * @param offset The offset of the block in the data set as number of elements (zero-based).
     * @param memoryOffset The offset of the block in <var>buffer</var> as number of elements
     *            (zero-based).
     * @return The effective block size.
     * @throws HDF5JavaException If the data set is not of rank 1.
     */
    public int readAsByteArrayToBlockWithOffset(final String objectPath, final byte[] buffer,
            final int blockSize, final long offset, final int memoryOffset)
            throws HDF5JavaException;

    /**
     * Provides all natural blocks of this one-dimensional data set to iterate over.
     * <em>Must not be called for data sets of rank other than 1!</em>
     * 
     * @see HDF5DataBlock
     * @throws HDF5JavaException If the data set is not of rank 1.
     */
    public Iterable<HDF5DataBlock<byte[]>> getAsByteArrayNaturalBlocks(final String dataSetPath)
            throws HDF5JavaException;

}