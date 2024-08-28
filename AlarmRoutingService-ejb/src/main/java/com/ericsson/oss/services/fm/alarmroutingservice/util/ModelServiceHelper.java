/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeAssociation;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.ModelService;
import com.ericsson.oss.itpf.modeling.modelservice.direct.DirectModelAccess;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeMember;

/**
 * Helper class to retrieve Information from ModelService.
 */
@ApplicationScoped
public class ModelServiceHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelServiceHelper.class);

    @Inject
    private ModelService modelService;

    /**
     * Reads the given EDT type of model with name and version using ModelService and returns list of all the members defined in the model.
     * @param modelName
     *            The model name to be read from model service
     * @param modelVersion
     *            The model version to be read from model service
     * @return returns list of EDT models information.
     */
    public List<String> readEdtModelInformation(final String edtSchema, final String nameSpace, final String modelName, final String modelVersion) {
        final List<String> edtModels = new ArrayList<String>();
        try {
            final DirectModelAccess directModelAccess = modelService.getDirectAccess();
            final ModelInfo modelInfo = new ModelInfo(edtSchema, nameSpace, modelName, modelVersion);
            final EnumDataTypeDefinition eventTypeDef = directModelAccess.getAsJavaTree(modelInfo, EnumDataTypeDefinition.class);
            final Iterator<EnumDataTypeMember> eventAttrs = eventTypeDef.getMember().iterator();

            while (eventAttrs.hasNext()) {
                final EnumDataTypeMember enumDataTypeMember = eventAttrs.next();
                edtModels.add(enumDataTypeMember.getName());
            }
            LOGGER.debug("EventAttributes from ModelService {}", edtModels);
        } catch (final Exception exception) {
            LOGGER.error("Exception in accessing ModelService : ", exception);
        }
        return edtModels;
    }

    public List<PrimaryTypeAssociation> readEndpointAssociations(final String relationshipSchema, final String nameSpace, final String modelName,
                                                                 final String modelVersion) {
        final List<PrimaryTypeAssociation> primaryTypeAssociations = new ArrayList<PrimaryTypeAssociation>();
        try {
            final DirectModelAccess directModelAccess = modelService.getDirectAccess();
            final ModelInfo modelInfo = new ModelInfo(relationshipSchema, nameSpace, modelName, modelVersion);
            final PrimaryTypeRelationshipDefinition eventTypeDef = directModelAccess
                    .getAsJavaTree(modelInfo, PrimaryTypeRelationshipDefinition.class);
            if (null != eventTypeDef && null != eventTypeDef.getAssociation()) {
                return eventTypeDef.getAssociation();
            }
        } catch (final Exception exception) {
            LOGGER.error("Exception in accessing ModelService : ", exception);
        }
        return primaryTypeAssociations;
    }
}
