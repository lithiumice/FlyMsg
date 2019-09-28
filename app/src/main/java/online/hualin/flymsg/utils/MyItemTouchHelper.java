//package online.hualin.flymsg.utils;
//
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {
//    private ItemTouchMoveCallback mMoveCallback;
//
//    public MyItemTouchHelperCallback(ItemTouchMoveCallback callback) {
//        this.mMoveCallback = callback;
//    }
//
//    //Callback回调监听时先调用的，用来判断当前是什么动作，比如判断方向（监听哪个方向的拖动）
//    @Override
//    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        //放向：up、down、left、right
//        //常量：
////        int up = ItemTouchHelper.UP;//1  0x0001
////        int down = ItemTouchHelper.DOWN;//2 0x0010
////        int left = ItemTouchHelper.LEFT;
////        int right = ItemTouchHelper.RIGHT;
//
//        //要监听的拖拽方向，不监听为0
//        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//        //要监听的侧滑方向，不监听为0
////        int swipeFlags = 0;
//        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
//        int flags = makeMovementFlags(dragFlags, swipeFlags);
//        return flags;//即监听向上也监听向下
//    }
//
//    //是否允许长按拖拽
//    @Override
//    public boolean isLongPressDragEnabled() {
//        return true;
//    }
//
//    //移动的时候回调的方法（拖拽等）
//    @Override
//    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//        if (viewHolder.getItemViewType() != target.getItemViewType()) {
//            return false;
//        }
//        //在拖拽的过程中不断地调用adapter.notifyItemMoved(from,to)
//        mMoveCallback.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//        return true;
//    }
//
//    //侧滑的时候回调的方法
//    @Override
//    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        //监听侧滑；1.删除数据，2.调用adapter.notifyItemRemoved(position)
//        mMoveCallback.onItemRemove(viewHolder.getAdapterPosition());
//    }
//
//    //改变选中的Item
//    @Override
//    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//        //判断状态
//        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//            viewHolder.itemView.findViewById(R.id.textview).setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.black));
//        }
//        super.onSelectedChanged(viewHolder, actionState);
//    }
//
//    //恢复改变选中的Item
//    @Override
//    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        //恢复
//        viewHolder.itemView.findViewById(R.id.textview).setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.colorPrimary));
//        //透明度动画
//        viewHolder.itemView.setAlpha(1);//1~0
//        //缩放动画
//        viewHolder.itemView.setScaleX(1);//1~0
//        viewHolder.itemView.setScaleY(1);//1~0
//        super.clearView(recyclerView, viewHolder);
//    }
//
//    //在拖拽的时候做效果
//    @Override
//    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        //dx  水平方向移动的增量（负：向左；正：向右）范围：0~View.getWidth
//        float alpha=1 - Math.abs(dX) / viewHolder.itemView.getWidth();
//        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//            //透明度动画
//            viewHolder.itemView.setAlpha(alpha);//1~0
//            //缩放动画
//            viewHolder.itemView.setScaleX(alpha);//1~0
//            viewHolder.itemView.setScaleY(alpha);//1~0
//        }
////        if (alpha==0){
////            //透明度动画
////            viewHolder.itemView.setAlpha(1);//1~0
////            //缩放动画
////            viewHolder.itemView.setScaleX(1);//1~0
////            viewHolder.itemView.setScaleY(1);//1~0
////        }
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//    }
//}