package com.dotmarketing.beans;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI.PermissionableType;
import com.dotmarketing.business.PermissionSummary;
import com.dotmarketing.business.Permissionable;
import com.dotmarketing.business.RelatedPermissionableGroup;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.util.Logger;

import java.util.List;
import java.util.Objects;

public  class PermissionableProxy implements Permissionable {
	
	public String getType() {
		return type;
	}

	public boolean isHost() {

		if ( Objects.equals(PermissionableType.CONTENTLETS.getCanonicalName(), this.type) ) {

			//TODO: Find a better way
			//FIXME: Find a better way

			Contentlet contentlet = null;

			if ( permissionByIdentifier ) {

				try {
					contentlet = APILocator.getContentletAPI()
							.findContentletByIdentifier(getIdentifier(), false, -1,
									APILocator.getUserAPI().getSystemUser(), false);

				} catch (Exception e) {
					Logger.error(this.getClass(), "Error searching for contentlet [" + getIdentifier() + "].", e);
				}

			} else {
				try {
					contentlet = APILocator.getContentletAPI()
							.find(getInode(), APILocator.getUserAPI().getSystemUser(), false);

				} catch (Exception e) {
					Logger.error(this.getClass(), "Error searching for contentlet [" + getInode() + "].", e);
				}
			}

			if ( null != contentlet ) {
				return contentlet.isHost();
			}

			//FIXME: Find a better way
			//TODO: Find a better way
		}

		return false;
	}

	public void setType(String type) {
		this.type = type;
		if(type ==null) return;
		if(type.equals("contentlet") || type.equals("host")) {
			this.type = PermissionableType.CONTENTLETS.getCanonicalName();
			setPermissionByIdentifier(true);
			if ( this.isHost() ) {
				this.isParentPermissionable = true;
			}
		} else if (type.equals("htmlpage")) {
			this.type = PermissionableType.HTMLPAGES.getCanonicalName();
			setPermissionByIdentifier(true);
			this.isParentPermissionable = true;
		} else if (type.equals("template")) {
			this.type = PermissionableType.TEMPLATES.getCanonicalName();
			setPermissionByIdentifier(true);
		} else if (type.equals("containers")) {
			this.type = PermissionableType.CONTAINERS.getCanonicalName();
			setPermissionByIdentifier(true);
		} else if (type.equals("folder")) {
			this.type = PermissionableType.FOLDERS.getCanonicalName();
			setPermissionByIdentifier(false);
			this.isParentPermissionable = true;
		} else if (type.equals("structure")) {
			this.type = PermissionableType.STRUCTURES.getCanonicalName();
			setPermissionByIdentifier(false);
			this.isParentPermissionable = true;
		} else if (type.equals("category")) {
			this.type = PermissionableType.CATEGORY.getCanonicalName();
			setPermissionByIdentifier(false);
			this.isParentPermissionable = true;
		} 
	}

	private static final long serialVersionUID = 1L;
	
	public String Inode =null;
	
	public String Identifier =null;
	
	public Boolean permissionByIdentifier =true;
	
	public String type =null;
	
	public String owner =null;

	public Boolean isParentPermissionable = false;
	
	public Boolean getPermissionByIdentifier() {
		return permissionByIdentifier;
	}

	public void setPermissionByIdentifier(Boolean permissionByIdentifier) {
		this.permissionByIdentifier = permissionByIdentifier;
	}

	public String getInode() {
		return Inode;
	}

	public String getIdentifier() {
		return Identifier;
	}

	public void setInode(String inode) {
		Inode = inode;
	}

	public void setIdentifier(String identifier) {
		Identifier = identifier;
	}
	
	public String getPermissionId() {
		String idReturn=null;
		if(permissionByIdentifier){
			idReturn=getIdentifier();
		}
		else{
			idReturn=getInode();
		}
		return idReturn;
	}

	public List<PermissionSummary> acceptedPermissions() {
		return null;
	}

	public String getOwner() {
		return owner;
	}

	public Permissionable getParentPermissionable() throws DotDataException {
		return null;
	}

	public String getPermissionType() {
		return type;
	}

	public List<RelatedPermissionableGroup> permissionDependencies(
			int requiredPermission) {
		return null;
	}

	public void setOwner(String owner) {
		this.owner=owner;
	}

	public boolean isParentPermissionable() {
		return isParentPermissionable;
	}

}
