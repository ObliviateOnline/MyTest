package com.example.mytest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.webkit.WebView;
import android.widget.LinearLayout;

import java.net.URISyntaxException;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatImageView vMessageButtonClose = findViewById(R.id.vMessageButtonClose);

        vMessageButtonClose.post(new Runnable() {
            @Override
            public void run() {
                clickMessageButtonCloseAnimation();
            }
        });


    }

    /**
     * 动画效果实现 AnimationSet
     */
    private void clickMessageButtonCloseAnimation() {
        LinearLayout vLlMessageButton = findViewById(R.id.vLlMessageButton);
        AppCompatImageView vMessageButtonClose = findViewById(R.id.vMessageButtonClose);
        AppCompatImageView vAlarm = findViewById(R.id.vAlarm);
        float sc = 44/80f;
        // 创建移动动画
        // 获取当前位置的 View 中心点坐标
        int[] startLocation = new int[2];
        vLlMessageButton.getLocationOnScreen(startLocation);
        float startX = startLocation[0] +vLlMessageButton.getWidth() / 2f;
        float startY = startLocation[1] + vLlMessageButton.getHeight() / 2f;

        // 获取目标位置的 View 中心点坐标，并考虑到缩放因子
        int[] endLocation = new int[2];
        vAlarm.getLocationOnScreen(endLocation);
        float endX = endLocation[0] + vAlarm.getWidth() * sc;
        float endY = endLocation[1] + vAlarm.getHeight() * sc / 2f;

        // 计算需要移动的距离
        float deltaX = endX - startX;
        float deltaY = endY - startY;

        // 创建第一个动画，将 View 移动到目标位置并同时进行缩放
        ObjectAnimator moveAnimatorX = ObjectAnimator.ofFloat(vLlMessageButton, "translationX", 0f, deltaX);
        ObjectAnimator moveAnimatorY = ObjectAnimator.ofFloat(vLlMessageButton, "translationY", 0f, deltaY);
        ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(vLlMessageButton, "scaleX", sc);
        ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(vLlMessageButton, "scaleY", sc);
        AnimatorSet firstAnimatorSet = new AnimatorSet();
        firstAnimatorSet.playTogether(moveAnimatorX, moveAnimatorY, scaleAnimatorX, scaleAnimatorY);
        firstAnimatorSet.setDuration(500);
        firstAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        //监听动画开始，设置alarm图标缩放动画，以及在动画过程中将红点暂时隐藏
        firstAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //与此同时，alarm图标动画开始： 1.缩小到消失（和悬浮图标同步）2.再次显示，alarm图标进行摇动
                Animation scale = new ScaleAnimation(1f, 0, 1f, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scale.setDuration(100);
                AnimationSet animSet = new AnimationSet(true);
                animSet.addAnimation(scale);
                vAlarm.startAnimation(animSet);
                animSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        vLlMessageButton.setVisibility(View.GONE);

                        //alarm图标动画： 1.缩小到消失（和悬浮图标同步）2.再次显示，alarm图标进行摇动
                        Animation shake = new RotateAnimation(-3, 3, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f);
                        shake.setInterpolator(new CycleInterpolator(3));
                        shake.setDuration(1000);
                        vAlarm.startAnimation(shake);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        // 创建第二个动画，将 View 缩放至 0
        ObjectAnimator secondScaleAnimatorX = ObjectAnimator.ofFloat(vLlMessageButton, "scaleX", 0f);
        ObjectAnimator secondScaleAnimatorY = ObjectAnimator.ofFloat(vLlMessageButton, "scaleY", 0f);
        secondScaleAnimatorX.setDuration(100);
        secondScaleAnimatorY.setDuration(100);
        secondScaleAnimatorX.setInterpolator(new AccelerateDecelerateInterpolator());
        secondScaleAnimatorY.setInterpolator(new AccelerateDecelerateInterpolator());
        // 创建 AnimatorSet，将两个动画组合起来
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(firstAnimatorSet).before(secondScaleAnimatorX).before(secondScaleAnimatorY);

        // 按钮点击事件
        vMessageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vMessageButtonClose.setVisibility(View.GONE);
                // 启动动画
                animatorSet.start();
            }
        });
    }

}