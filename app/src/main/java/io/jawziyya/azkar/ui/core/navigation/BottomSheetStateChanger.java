/*
 * Copyright (C) 2020 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jawziyya.azkar.ui.core.navigation;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import timber.log.Timber;

public class BottomSheetStateChanger {
    private final Handler handler = new Handler(Looper.getMainLooper());

    protected final FragmentManager fragmentManager;
    protected final int containerId;

    /**
     * Constructor.
     *
     * @param fragmentManager the fragment manager
     * @param containerId     the container ID in which fragments are swapped
     */
    public BottomSheetStateChanger(@Nonnull FragmentManager fragmentManager, @IdRes int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    /**
     * Forward navigation handler. Horizontal translation animation by default.
     *
     * @param fragmentTransaction the fragment transaction
     * @param stateChange         the state change
     */
    protected void onForwardNavigation(@Nonnull FragmentTransaction fragmentTransaction, @Nonnull StateChange stateChange) {
//        fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    /**
     * Backward navigation handler. Horizontal translation animation by default.
     *
     * @param fragmentTransaction the fragment transaction
     * @param stateChange         the state change
     */
    protected void onBackwardNavigation(@Nonnull FragmentTransaction fragmentTransaction, @Nonnull StateChange stateChange) {
//        fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * Replace navigation handler. Open transition animation by default.
     *
     * @param fragmentTransaction the fragment transaction
     * @param stateChange         the state change
     */
    protected void onReplaceNavigation(@Nonnull FragmentTransaction fragmentTransaction, @Nonnull StateChange stateChange) {
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    }

    /**
     * Decide if the fragment is currently added, but not showing.
     *
     * @param fragment the fragment
     * @return if the fragment is not showing
     */
    protected boolean isNotShowing(@Nonnull Fragment fragment) {
        return fragment.isDetached();
    }

    /**
     * Start showing the fragment that is added but not showing.
     *
     * @param fragmentTransaction the fragment transaction
     * @param fragment            the fragment
     */
    protected void startShowing(@Nonnull FragmentTransaction fragmentTransaction, @Nonnull Fragment fragment) {
        fragmentTransaction.attach(fragment); // show top fragment if already exists
    }

    /**
     * Stop showing the fragment that is added, showing, but should not be showing.
     *
     * @param fragmentTransaction the fragment transaction
     * @param fragment            the fragment
     */
    protected void stopShowing(@Nonnull FragmentTransaction fragmentTransaction, @Nonnull Fragment fragment) {
        fragmentTransaction.detach(fragment); // destroy view of fragment not top
    }

    /**
     * Overriding this callback allows for additional configurations to be made to the FragmentTransaction.
     *
     * @param fragmentTransaction the fragment transaction
     * @param topPreviousFragment the top previous fragment
     * @param topNewFragment the top new fragment (might not be added yet!)
     * @param stateChange the state change
     */
    protected void configureFragmentTransaction(@Nonnull FragmentTransaction fragmentTransaction, @Nullable Fragment topPreviousFragment, @Nonnull Fragment topNewFragment, @Nonnull StateChange stateChange) {
    }

    /**
     * Handles the transition from one state to another, swapping fragments.
     *
     * @param stateChange the state change
     */
    public final void handleStateChange(@Nonnull final StateChange stateChange) {
        boolean didExecutePendingTransactions = false;

        try {
            fragmentManager.executePendingTransactions(); // two synchronous immediate fragment transactions can overlap.
            didExecutePendingTransactions = true;
        } catch(IllegalStateException e) { // executePendingTransactions() can fail, but this should "just work".
        }

        if(didExecutePendingTransactions) {
            executeFragmentTransaction(stateChange);
        } else { // failed to execute pending transactions
            if(!fragmentManager.isDestroyed()) { // ignore state change if activity is dead. :(
                handler.post(new Runnable() { // run with delay if already executing transactions -.-
                    @Override
                    public void run() {
                        if(!fragmentManager.isDestroyed()) { // ignore state change if activity is dead. :(
                            executeFragmentTransaction(stateChange);
                        }
                    }
                });
            }
        }
    }

    private void executeFragmentTransaction(@Nonnull StateChange stateChange) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack();
        if(stateChange.getDirection() == StateChange.FORWARD) {
            onForwardNavigation(fragmentTransaction, stateChange);
        } else if(stateChange.getDirection() == StateChange.BACKWARD) {
            onBackwardNavigation(fragmentTransaction, stateChange);
        } else { // REPLACE
            onReplaceNavigation(fragmentTransaction, stateChange);
        }

        FragmentKey topPreviousKey = stateChange.topPreviousKey();
        Fragment topPreviousFragment = null;

        if(topPreviousKey != null) {
            topPreviousFragment = fragmentManager.findFragmentByTag(topPreviousKey.getFragmentTag());
        }

        FragmentKey topNewKey = stateChange.topNewKey();

        Fragment topNewFragment = fragmentManager.findFragmentByTag(topNewKey.getFragmentTag());

        if(topNewFragment == null || topNewFragment.isRemoving()) {
            topNewFragment = topNewKey.createFragment(); // create new fragment here, ahead of time
        }

        configureFragmentTransaction(fragmentTransaction, topPreviousFragment, topNewFragment, stateChange);

        List<FragmentKey> previousKeys = stateChange.getPreviousKeys();
        List<FragmentKey> newKeys = stateChange.getNewKeys();
//        for(FragmentKey oldKey : previousKeys) {
//            Fragment fragment = fragmentManager.findFragmentByTag(oldKey.getFragmentTag());
//            if(fragment != null) {
//                if(!newKeys.contains(oldKey)) {
//                    fragmentTransaction.remove(fragment); // remove fragments not in backstack
//                } else if(!isNotShowing(fragment)) {
//                    stopShowing(fragmentTransaction, fragment);
//                }
//            }
//        }
        for (int i = 0; i < previousKeys.size(); i++) {
            FragmentKey oldKey = previousKeys.get(i);

            Fragment fragment = fragmentManager.findFragmentByTag(oldKey.getFragmentTag());
            if(fragment != null) {
                if(!newKeys.contains(oldKey)) {
                    fragmentTransaction.remove(fragment); // remove fragments not in backstack
                } else if(!isNotShowing(fragment)) {
                    if (oldKey != topPreviousKey || previousKeys.size() != newKeys.size() - 1 || !topNewKey.getBottomSheet()) {
                        Timber.d("oldKey != topPreviousKey === %s", (oldKey != topPreviousKey));
                        Timber.d("previousKeys.size() != newKeys.size() - 1 === %s", (previousKeys.size() != newKeys.size() - 1));
                        Timber.d("!topNewKey.getBottomSheet() === %s", (!topNewKey.getBottomSheet()));
                        Timber.d("previousKeys, stopShowing=%s", fragment.getTag());
                        stopShowing(fragmentTransaction, fragment);
                    }
                }
            }
        }
//        for(FragmentKey newKey : newKeys) {
//            final Fragment fragment = fragmentManager.findFragmentByTag(newKey.getFragmentTag());
//            if(newKey.equals(stateChange.topNewKey())) {
//                if(fragment != null) {
//                    if(fragment.isRemoving()) { // fragments are quirky, they die asynchronously. Ignore if they're still there.
//                        fragmentTransaction.replace(containerId, topNewFragment, newKey.getFragmentTag());
//                    } else if(isNotShowing(fragment)) {
//                        startShowing(fragmentTransaction, fragment);
//                    }
//                } else {
//                    // add the newly created fragment if it's not there yet
//                    fragmentTransaction.add(containerId, topNewFragment, newKey.getFragmentTag());
//                }
//            } else {
//                if(fragment != null && !isNotShowing(fragment)) {
//                    stopShowing(fragmentTransaction, fragment);
//                }
//            }
//        }

        for (int i = 0; i < newKeys.size(); i++) {
            FragmentKey newKey = newKeys.get(i);

            final Fragment fragment = fragmentManager.findFragmentByTag(newKey.getFragmentTag());
            if(newKey.equals(stateChange.topNewKey())) {
                if(fragment != null) {
                    if(fragment.isRemoving()) { // fragments are quirky, they die asynchronously. Ignore if they're still there.
                        fragmentTransaction.replace(containerId, topNewFragment, newKey.getFragmentTag());
                    } else if(isNotShowing(fragment)) {
                        startShowing(fragmentTransaction, fragment);
                    }
                } else {
                    // add the newly created fragment if it's not there yet
                    fragmentTransaction.add(containerId, topNewFragment, newKey.getFragmentTag());
                }
            } else {
//                if(fragment != null && !isNotShowing(fragment)) {
                FragmentKey nextKey = newKeys.get(i + 1);
                Timber.d(nextKey.toString() + " " + nextKey.getBottomSheet());
                if (fragment != null && !isNotShowing(fragment) && !nextKey.getBottomSheet()) {
                    Timber.d("newKeys, stopShowing=%s", fragment.getTag());
                    stopShowing(fragmentTransaction, fragment);
                }
            }
        }

        fragmentTransaction.commitAllowingStateLoss();
    }
}
