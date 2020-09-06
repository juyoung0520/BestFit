package com.example.bestfit.util

import android.content.Context
import com.example.bestfit.R
import com.qingmei2.rximagepicker.entity.Result
import com.qingmei2.rximagepicker.entity.sources.Camera
import com.qingmei2.rximagepicker.entity.sources.Gallery
import com.qingmei2.rximagepicker.ui.ICustomPickerConfiguration
import com.qingmei2.rximagepicker_extension_zhihu.ui.ZhihuImagePickerActivity
import com.qingmei2.rximagepicker_extension_zhihu.ui.ZhihuImagePickerFragment

import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_add_item.*

interface ImagePicker {

    @Gallery(componentClazz = ZhihuImagePickerActivity::class,
            openAsFragment = false)
    fun openGalleryAsNormal(context: Context,
                            config: ICustomPickerConfiguration): Observable<Result>

    @Camera
    fun openCamera(context: Context): Observable<Result>
}
