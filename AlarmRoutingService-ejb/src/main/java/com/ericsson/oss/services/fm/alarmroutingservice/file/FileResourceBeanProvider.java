/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.file;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 * Class provides file resource beans based on the file type.
 */
@Stateless
public class FileResourceBeanProvider {

    @Inject
    @Any
    private Instance<FileResource> fileResourceProvider;

    public FileResource getImplementationBean(final String fileType) {
        return fileResourceProvider.select(getAnnotation(fileType)).get();
    }

    private AnnotationLiteral getAnnotation(final String fileType) {
        return FileResourceType.valueOf(fileType.toUpperCase()).getAnnotation();
    }

    /**
     * ENUM for File Resource Bean Provider based on type.
     */
    enum FileResourceType {
        TXT {
            @Override
                    AnnotationLiteral getAnnotation() {
                return new AnnotationLiteral<TextFileResourceQualifier>() {};
            }
        },
        CSV {
            @Override
                    AnnotationLiteral getAnnotation() {
                return new AnnotationLiteral<CsvFileResourceQualifier>() {};
            }
        };

        abstract AnnotationLiteral getAnnotation();
    }
}
