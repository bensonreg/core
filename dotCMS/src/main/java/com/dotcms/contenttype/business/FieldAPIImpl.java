package com.dotcms.contenttype.business;

import java.util.List;

import com.dotcms.contenttype.exception.NotFoundInDbException;
import com.dotcms.contenttype.model.field.BinaryField;
import com.dotcms.contenttype.model.field.CategoryField;
import com.dotcms.contenttype.model.field.CheckboxField;
import com.dotcms.contenttype.model.field.ConstantField;
import com.dotcms.contenttype.model.field.CustomField;
import com.dotcms.contenttype.model.field.DateField;
import com.dotcms.contenttype.model.field.DateTimeField;
import com.dotcms.contenttype.model.field.Field;
import com.dotcms.contenttype.model.field.FieldVariable;
import com.dotcms.contenttype.model.field.FileField;
import com.dotcms.contenttype.model.field.HiddenField;
import com.dotcms.contenttype.model.field.HostFolderField;
import com.dotcms.contenttype.model.field.ImageField;
import com.dotcms.contenttype.model.field.KeyValueField;
import com.dotcms.contenttype.model.field.LineDividerField;
import com.dotcms.contenttype.model.field.MultiSelectField;
import com.dotcms.contenttype.model.field.PermissionTabField;
import com.dotcms.contenttype.model.field.RadioField;
import com.dotcms.contenttype.model.field.RelationshipsTabField;
import com.dotcms.contenttype.model.field.SelectField;
import com.dotcms.contenttype.model.field.TabDividerField;
import com.dotcms.contenttype.model.field.TagField;
import com.dotcms.contenttype.model.field.TextAreaField;
import com.dotcms.contenttype.model.field.TimeField;
import com.dotcms.contenttype.model.field.WysiwygField;
import com.dotcms.contenttype.model.type.ContentType;
import com.dotcms.contenttype.transform.contenttype.StructureTransformer;
import com.dotcms.repackage.com.google.common.collect.ImmutableList;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.PermissionLevel;
import com.dotmarketing.cache.FieldsCache;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.structure.factories.StructureFactory;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.services.ContentletMapServices;
import com.dotmarketing.services.ContentletServices;
import com.dotmarketing.services.StructureServices;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;


public class FieldAPIImpl implements FieldAPI {

  private List<Class> baseFieldTypes = ImmutableList.of(BinaryField.class, CategoryField.class,
      ConstantField.class, CheckboxField.class, CustomField.class, DateField.class,
      DateTimeField.class, FileField.class, HiddenField.class, HostFolderField.class,
      ImageField.class, KeyValueField.class, LineDividerField.class, MultiSelectField.class,
      PermissionTabField.class, RadioField.class, RelationshipsTabField.class, SelectField.class,
      TabDividerField.class, TagField.class, TextAreaField.class, TimeField.class,
      WysiwygField.class);

  private PermissionAPI perAPI = APILocator.getPermissionAPI();
  private ContentletAPI conAPI = APILocator.getContentletAPI();

  private FieldFactory fac = new FieldFactoryImpl();

  @Override
	public Field save(Field field, User user) throws DotDataException, DotSecurityException {
		ContentTypeAPI tapi = APILocator.getContentTypeAPI(user);
		ContentType type = tapi.find(field.contentTypeId()) ;
		perAPI.checkPermission(type, PermissionLevel.PUBLISH, user);

	    Field oldField = null;
	    if (UtilMethods.isSet(field.id())) {
	    	oldField = fac.byId(field.id());
	    }

		Field result = fac.save(field);


		Structure structure = new StructureTransformer(type).asStructure();

        StructureServices.removeStructureFile(structure);

        //Refreshing permissions
        if(field instanceof HostFolderField){
        	perAPI.resetChildrenPermissionReferences(structure);
        }

        //http://jira.dotmarketing.net/browse/DOTCMS-5178
        if(oldField != null && ((!oldField.indexed() && field.indexed()) || (oldField.indexed() && !field.indexed()))){
          // rebuild contentlets indexes
          conAPI.reindex(structure);
        }

        if (field instanceof ConstantField) {
            ContentletServices.removeContentletFile(structure);
            ContentletMapServices.removeContentletMapFile(structure);
            conAPI.refresh(structure);
        }

        return result;
	}
  
  @Override
  public FieldVariable save(FieldVariable var, User user) throws DotDataException, DotSecurityException {
      ContentTypeAPI tapi = APILocator.getContentTypeAPI(user);
      Field field = fac.byId(var.fieldId());

      ContentType type = tapi.find(field.contentTypeId()) ;
      APILocator.getPermissionAPI().checkPermission(type, PermissionLevel.PUBLISH, user);
      

      return fac.save(var);
  }
  @Override
  public void delete(Field field) throws DotDataException {
    fac.delete(field);
  }


  @Override
  public List<Field> byContentTypeId(String typeId) throws DotDataException {
    return fac.byContentTypeId(typeId);
  }
  @Override
  public String nextAvailableColumn(Field field) throws DotDataException{
      return fac.nextAvailableColumn(field);
  }
  @Override
  public Field find(String id) throws DotDataException {
    return fac.byId(id);
  }

  @Override
  public Field byContentTypeAndVar(ContentType type, String fieldVar) throws DotDataException {
    return fac.byContentTypeFieldVar(type, fieldVar);
  }
  
  @Override
  public Field byContentTypeIdAndVar(String id, String fieldVar) throws DotDataException {
    return fac.byContentTypeIdFieldVar(id, fieldVar);
  }
  
  @Override
  public void deleteFieldsByContentType(ContentType type) throws DotDataException {
    fac.deleteByContentType(type);
  }

  @Override
  public List<Class> fieldTypes() {
    return baseFieldTypes;
  }

  @Override
  public void registerFieldType(Field type) {
    throw new DotStateException("Not implemented");
  }

  @Override
  public void deRegisterFieldType(Field type) {
    throw new DotStateException("Not implemented");
  }

@Override
public void delete(FieldVariable fieldVar) throws DotDataException {
    fac.delete(fieldVar);
    
}

@Override
public List<FieldVariable> loadVariables(Field field) throws DotDataException {
    return fac.loadVariables(field);
}

@Override
public FieldVariable loadVariable(String id) throws DotDataException {
    return fac.loadVariable(id);
}
  
  
  
  
  
  
}
