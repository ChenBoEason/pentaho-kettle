/*
 * Copyright 2017-2019 Hitachi Vantara. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

package org.pentaho.di.ui.core;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPointHandler;
import org.pentaho.di.core.extension.KettleExtensionPoint;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryObjectInterface;
import org.pentaho.di.ui.core.widget.TextVar;

import java.util.function.BiConsumer;

/**
 * Created by bmorrise on 8/17/17.
 */
public class FileDialogOperation {

  public static final String SELECT_FOLDER = "selectFolder";
  public static final String SELECT_FILE = "selectFile";
  public static final String OPEN = "open";
  public static final String SAVE = "save";
  public static final String ORIGIN_SPOON = "spoon";
  public static final String ORIGIN_OTHER = "other";
  public static final String TRANSFORMATION = "transformation";
  public static final String JOB = "job";
  public static final String PROVIDER_REPO = "repository";

  private Repository repository;
  private String command;
  private String filter;
  private String defaultFilter;
  private String origin;
  private RepositoryObjectInterface repositoryObject;
  private String startDir;
  private String title;
  private String filename;
  private String fileType;
  private String path;
  private String connection;
  private String provider;
  private String providerFilter;
  private boolean useSchemaPath;

  public FileDialogOperation( String command ) {
    this.command = command;
  }

  public FileDialogOperation( String command, String origin ) {
    this.command = command;
    this.origin = origin;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand( String command ) {
    this.command = command;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter( String filter ) {
    this.filter = filter;
  }

  public String getDefaultFilter() {
    return defaultFilter;
  }

  public void setDefaultFilter( String defaultFilter ) {
    this.defaultFilter = defaultFilter;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin( String origin ) {
    this.origin = origin;
  }

  public RepositoryObjectInterface getRepositoryObject() {
    return repositoryObject;
  }

  public void setRepositoryObject( RepositoryObjectInterface repositoryObject ) {
    this.repositoryObject = repositoryObject;
  }

  public String getStartDir() {
    return startDir;
  }

  public void setStartDir( String startDir ) {
    this.startDir = startDir;
  }

  public Repository getRepository() {
    return repository;
  }

  public void setRepository( Repository repository ) {
    this.repository = repository;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle( String title ) {
    this.title = title;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename( String filename ) {
    this.filename = filename;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType( String fileType ) {
    this.fileType = fileType;
  }

  public String getPath() {
    return path;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  public String getConnection() {
    return connection;
  }

  public void setConnection( String connection ) {
    this.connection = connection;
  }

  public boolean getUseSchemaPath() {
    return useSchemaPath;
  }

  public void setUseSchemaPath( boolean useSchemaPath ) {
    this.useSchemaPath = useSchemaPath;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider( String provider ) {
    this.provider = provider;
  }

  public String getProviderFilter() {
    return providerFilter;
  }

  public void setProviderFilter( String providerFilter ) {
    this.providerFilter = providerFilter;
  }

  public boolean isProviderRepository() {
    return provider.equalsIgnoreCase( PROVIDER_REPO );
  }

  public static void handleOpen( TextVar textVar, FileDialogOperation fileDialogOperation ) {
    String path = fileDialogOperation.getPath();
    if ( path != null ) {
      textVar.setText( fileDialogOperation.getPath() );
    }
  }

  public static void handleSave( TextVar textVar, FileDialogOperation fileDialogOperation ) {
    String path = fileDialogOperation.getPath();
    String fileName = fileDialogOperation.getFilename();
    if ( path != null && fileName != null ) {
      textVar.setText( fileDialogOperation.getPath() + "/" + fileDialogOperation.getFilename() );
    }
  }

  public static void setStartLocation( TextVar textVar, FileDialogOperation fileDialogOperation ) {
    String fullPath = textVar.getText();
    if ( !Utils.isEmpty( fullPath ) ) {
      fileDialogOperation
        .setPath( fullPath.substring( 0, fullPath.lastIndexOf( '/' ) ) );
      fileDialogOperation
        .setFilename( fullPath.substring( fullPath.lastIndexOf( '/' ) + 1 ) );
      if ( fullPath.startsWith( "hc://" ) ) {
        fileDialogOperation.setProvider( "clusters" );
      } else if ( fullPath.startsWith( "pvfs://" ) ) {
        fileDialogOperation.setProvider( "vfs" );
      } else if ( fullPath.startsWith( "/" ) ) {
        fileDialogOperation.setProvider( "local" );
      }
    }
  }

  public static void browse( String command, TextVar textVar, BiConsumer<TextVar, FileDialogOperation> before,
                             BiConsumer<TextVar, FileDialogOperation> after ) {
    FileDialogOperation fileDialogOperation = new FileDialogOperation( command );
    before.accept( textVar, fileDialogOperation );
    try {
      ExtensionPointHandler.callExtensionPoint( null, KettleExtensionPoint.SpoonOpenSaveNew.id, fileDialogOperation );
    } catch ( KettleException ignored ) {
      // Do nothing
    }
    after.accept( textVar, fileDialogOperation );
  }

  public static void browseForSave( TextVar textVar ) {
    browse( FileDialogOperation.SAVE, textVar, FileDialogOperation::setStartLocation, FileDialogOperation::handleSave );
  }

  public static void browseForOpen( TextVar textVar ) {
    browse( FileDialogOperation.OPEN, textVar, FileDialogOperation::setStartLocation, FileDialogOperation::handleOpen );
  }
}
