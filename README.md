# 红岩半期考核
### 重构app：将播放器从activity移动到service，解决了收藏bug
![](http://image.so.com/view?q=%E4%BF%AE%E4%BB%99%E7%86%AC%E5%A4%9C&src=tab_www&correct=%E4%BF%AE%E4%BB%99%E7%86%AC%E5%A4%9C&ancestor=list&cmsid=86ebfcad60e7efff1e5f8edb03046d37&cmran=0&cmras=0&cn=0&gn=0&kn=0&fsn=60#id=cf025c8d0603a83f2e01153ebcd1af0d&currsn=0)
[安装包](https://github.com/kiritoj/EIF/blob/master/app/release/app-release.apk)
## 效果图
### 保存头像昵称
![](http://ww1.sinaimg.cn/large/006nwaiFly1g373cnweuzg30a00jux6p.gif)
### 根据心情切歌，那个弹出控件没实现
![](http://ww1.sinaimg.cn/large/006nwaiFly1g373kd1fjng30a00jue81.gif)
### 显示歌词，切换上一曲，下一曲，暂停，继续，拖动进度条本来实现了，但是暂停后继续又出问题了，改回去了。歌词直接放进去的，没做处理。。。
![](http://ww1.sinaimg.cn/large/006nwaiFly1g373m1mpdzg30a00jue7h.gif)
### 收藏逻辑有问题，每次得出去一次才能收藏，还没来得及改
![](http://ww1.sinaimg.cn/large/006nwaiFly1g373feiefog30a00jue81.gif)
