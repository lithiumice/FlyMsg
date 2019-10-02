package online.hualin.flymsg.injector.annotation;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Description：TODO
 * Create Time：2016/10/715:33
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
@Scope @Retention(RUNTIME)
public @interface PerActivity {
}
