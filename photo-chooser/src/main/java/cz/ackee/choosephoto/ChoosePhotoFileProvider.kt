package cz.ackee.choosephoto

import androidx.core.content.FileProvider

/**
 * This file provider defines directory in which app can store photos. Referencing
 * multiple file providers in androidManifest where each define FileProvider as its
 * implementation class leads to compile errors. Simply subclassing and referencing the subclass
 * is enough to fix the problem.
 *
 * Defined by [res/xml/file_provider_paths.xml]
 */
class ChoosePhotoFileProvider : FileProvider()