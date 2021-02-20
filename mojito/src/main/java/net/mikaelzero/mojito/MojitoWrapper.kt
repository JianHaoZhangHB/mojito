package net.mikaelzero.mojito

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import net.mikaelzero.mojito.bean.ActivityConfig
import net.mikaelzero.mojito.bean.ViewParams
import net.mikaelzero.mojito.interfaces.ActivityCoverLoader
import net.mikaelzero.mojito.interfaces.IIndicator
import net.mikaelzero.mojito.interfaces.IProgress
import net.mikaelzero.mojito.interfaces.OnMojitoListener
import net.mikaelzero.mojito.loader.FragmentCoverLoader
import net.mikaelzero.mojito.loader.InstanceLoader
import net.mikaelzero.mojito.loader.MultiContentLoader
import net.mikaelzero.mojito.tools.DataWrapUtil
import net.mikaelzero.mojito.ui.ImageMojitoActivity

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/23 9:46 AM
 * @Description:
 */
class MojitoWrapper constructor(val context: Context?) {
    private val configBean = ActivityConfig()
    fun urls(imageUrl: String): MojitoWrapper {
        configBean.originImageUrls = listOf(imageUrl)
        return this
    }

    fun urls(imageUrl: String, targetUrl: String): MojitoWrapper {
        configBean.originImageUrls = listOf(imageUrl)
        configBean.targetImageUrls = listOf(targetUrl)
        return this
    }


    fun urls(imageUrls: List<String>?): MojitoWrapper {
        configBean.originImageUrls = imageUrls
        return this
    }

    fun urls(imageUrls: List<String>?, targetImageUrls: List<String>?): MojitoWrapper {
        configBean.originImageUrls = imageUrls
        configBean.targetImageUrls = targetImageUrls
        return this
    }

    fun position(position: Int, startPos: Int = 0): MojitoWrapper {
        configBean.startPos = startPos
        configBean.position = position - startPos
        return this
    }

    fun views(view: View?): MojitoWrapper {
        val views = arrayOfNulls<View>(1)
        views[0] = view
        return views(views)
    }

    fun views(recyclerView: RecyclerView, @IdRes viewId: Int): MojitoWrapper {
        val originImageViewList = mutableListOf<View?>()
        val childCount = recyclerView.childCount
        for (i in 0 until childCount) {
            val originImage = recyclerView.getChildAt(i).findViewById<View>(viewId)
            if (originImage != null) {
                originImageViewList.add(originImage)
            }
        }

        val newOriginImageViewList = mutableListOf<View?>()
        originImageViewList.forEachIndexed { index, view ->
            if (index >= configBean.startPos) {
                newOriginImageViewList.add(view)
            }
        }
        val views = arrayOfNulls<View>(newOriginImageViewList.size)
        for (i in newOriginImageViewList.indices) {
            views[i] = newOriginImageViewList[i]
        }
        return views(views)
    }


    fun views(views: Array<View?>): MojitoWrapper {
        val list = mutableListOf<ViewParams>()
        for (imageView in views) {
            val imageBean = ViewParams()
            if (imageView == null) {
                imageBean.left = 0
                imageBean.top = 0
                imageBean.width = 0
                imageBean.height = 0
            } else {
                val location = IntArray(2)
                imageView.getLocationOnScreen(location)
                imageBean.left = location[0]
                imageBean.top = location[1]
                imageBean.width = imageView.width
                imageBean.height = imageView.height
            }
            list.add(imageBean)
        }
        configBean.viewParams = list
        return this
    }

    fun autoLoadTarget(autoLoadTarget: Boolean): MojitoWrapper {
        configBean.autoLoadTarget = autoLoadTarget
        return this
    }

    /**
     * Listener start
     */
    fun setOnMojitoListener(onMojitoListener: OnMojitoListener): MojitoWrapper {
        ImageMojitoActivity.onMojitoListener = onMojitoListener
        return this
    }


    fun setProgressLoader(loader: InstanceLoader<IProgress>): MojitoWrapper {
        ImageMojitoActivity.progressLoader = loader
        return this
    }

    fun setFragmentCoverLoader(loader: InstanceLoader<FragmentCoverLoader>): MojitoWrapper {
        ImageMojitoActivity.fragmentCoverLoader = loader
        return this
    }

    fun setMultiContentLoader(loader: MultiContentLoader): MojitoWrapper {
        ImageMojitoActivity.multiContentLoader = loader
        return this
    }

    fun setActivityCoverLoader(on: ActivityCoverLoader): MojitoWrapper {
        ImageMojitoActivity.activityCoverLoader = on
        return this
    }

    fun setIndicator(on: IIndicator?): MojitoWrapper {
        ImageMojitoActivity.iIndicator = on
        return this
    }

    /**
     * Listener end
     */


    fun start() {
        assert()
        ImageMojitoActivity.hasShowedAnim = false
        DataWrapUtil.put(configBean)
        val activity = scanForActivity(context)
        val intent = Intent(activity, ImageMojitoActivity::class.java)
        activity?.startActivity(intent)
        activity?.overridePendingTransition(0, 0)
    }

    private fun assert() {
    }

    private fun scanForActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(context.baseContext)
        }
        return null
    }
}