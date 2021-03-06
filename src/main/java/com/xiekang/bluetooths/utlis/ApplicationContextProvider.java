package com.xiekang.bluetooths.utlis;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @项目名称 eHealthAPP
 * @类名 name：com.example.baseinstallation
 * @类描述 提供全局的上下文
 * @创建人 hsl201306
 * @创建时间 2020-02-20 12:05
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class ApplicationContextProvider extends ContentProvider {
  static Context mcontext;
  @Override
  public boolean onCreate() {
    mcontext=getContext();
    //初始化全局Context提供者
   ContextProvider.get();
    return false;
  }

  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
    return null;
  }

  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    return null;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
    return 0;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
    return 0;
  }
}
