#include "hdf5.h"
#include "h5utilJHDF5.h"
#include <jni.h>
#include <stdlib.h>
#include <string.h>

extern jboolean h5outOfMemory( JNIEnv *env, char *functName);
extern jboolean h5JNIFatalError( JNIEnv *env, char *functName);
extern jboolean h5nullArgument( JNIEnv *env, char *functName);
extern jboolean h5libraryError( JNIEnv *env );


/*
 * Class:     ch_systemsx_cisd_hdf5_hdf5lib_H5
 * Method:    getPointerSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_ch_systemsx_cisd_hdf5_hdf5lib_H5_getPointerSize
  (JNIEnv *env, jclass clss)
{
    return sizeof(void *);
}

/*
 * Class:     ch_systemsx_cisd_hdf5_hdf5lib_H5
 * Method:    compoundCpyVLStr
 * Signature: (Ljava/lang/String;[B)I)I
 */
JNIEXPORT jint JNICALL Java_ch_systemsx_cisd_hdf5_hdf5lib_H5_compoundCpyVLStr
  (JNIEnv *env, 
   jclass clss, 
   jstring str, /* IN: the string to copy */ 
   jbyteArray buf, /* OUT: array of byte */
   jint bufOfs /* The offset to copy the pointer to the string to. */
  )
{
    jbyte *byteP;
    char *strPCpy;
    int len;


    if ( str == NULL ) {
        h5nullArgument( env, "compoundCpyVLStr:  str is NULL");
        return -1;
    }
    if ( buf == NULL ) {
        h5nullArgument( env, "compoundCpyVLStr:  buf is NULL");
        return -1;
    }

	len = (*env)->GetStringUTFLength(env, str);
	strPCpy = calloc(1, len);
    (*env)->GetStringUTFRegion(env, str, 0, len, strPCpy);

    byteP = (*env)->GetPrimitiveArrayCritical(env, buf, NULL);
    if (byteP == NULL) {
        h5JNIFatalError( env, "compoundCpyVLStr:  buf not pinned");
        return -1;
    }
	*((char**)(byteP + bufOfs)) = strPCpy;
    (*env)->ReleasePrimitiveArrayCritical(env, buf, byteP, 0);

	return 0;
}

/*
 * Class:     ch_systemsx_cisd_hdf5_hdf5lib_H5
 * Method:    createVLStrFromCompound
 * Signature: ([B)I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_ch_systemsx_cisd_hdf5_hdf5lib_H5_createVLStrFromCompound
  (JNIEnv *env, 
   jclass clss, 
   jbyteArray buf, /* IN: array of byte containing the compound or compound array. */
   jint offset /* IN: The offset in the compound or compound array where the pointer to the string is located. */
  )
{
    void *byteP, *ptr;
    char **strP;
    jstring str;

    if ( buf == NULL ) {
        h5nullArgument( env, "createVLStrFromCompound:  buf is NULL");
        return NULL;
    }

    byteP = (*env)->GetPrimitiveArrayCritical(env, buf, NULL);
    if (byteP == NULL) {
        h5JNIFatalError( env, "createVLStrFromCompound:  buf not pinned");
        return NULL;
    }
    
	strP = byteP + offset;
	str = (*env)->NewStringUTF(env, *strP);
	
    (*env)->ReleasePrimitiveArrayCritical(env, buf, byteP, 0);
	
	return str;
}

/*
 * Class:     ch_systemsx_cisd_hdf5_hdf5lib_H5
 * Method:    freeCompoundVLStr
 * Signature: ([B)I[I))I
 */
JNIEXPORT jint JNICALL Java_ch_systemsx_cisd_hdf5_hdf5lib_H5_freeCompoundVLStr
  (JNIEnv *env, 
   jclass clss, 
   jbyteArray buf, /* IN: array of byte containing the compound or compound array. */
   jint recordSize, /* IN: The size of one compound record. */
   jintArray vlIndices /* IN: The indices of the variable-length compound members in the record. */
  )
{
    void *byteP, *ptr;
    char **strP;
    jsize bufLen, idxLen;
    int *idxP, i;

    if ( buf == NULL ) {
        h5nullArgument( env, "freeCompoundVLStr:  buf is NULL");
        return -1;
    }
    if ( vlIndices == NULL ) {
        h5nullArgument( env, "freeCompoundVLStr:  vlIndices is NULL");
        return -1;
    }

	idxLen = (*env)->GetArrayLength(env, vlIndices);
	bufLen = (*env)->GetArrayLength(env, buf);

    idxP = (*env)->GetPrimitiveArrayCritical(env, vlIndices, NULL);
    if (idxP == NULL) {
        h5JNIFatalError( env, "freeCompoundVLStr:  vlIndices not pinned");
        return -1;
    }
    byteP = (*env)->GetPrimitiveArrayCritical(env, buf, NULL);
    if (byteP == NULL) {
	    (*env)->ReleasePrimitiveArrayCritical(env, vlIndices, idxP, 0);
        h5JNIFatalError( env, "freeCompoundVLStr:  buf not pinned");
        return -1;
    }
    
	ptr = byteP;
	while (ptr - byteP < bufLen)
	{
	    for (i = 0; i < idxLen; ++i)
	    {
	    	strP = ptr + idxP[i];
	        free(*strP);
	    }
	    ptr += recordSize; 
	}
	
    (*env)->ReleasePrimitiveArrayCritical(env, vlIndices, idxP, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, buf, byteP, 0);
	
	return 0;
}