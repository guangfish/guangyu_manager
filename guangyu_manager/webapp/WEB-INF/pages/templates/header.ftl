<header class="main-header">
    <!-- Logo -->
    <a class="logo">
        <!-- mini logo for sidebar mini 50x50 pixels -->
        <span class="logo-mini"><b>A</b>LT</span>
        <!-- logo for regular state and mobile devices -->
        <span class="logo-lg"> 逛鱼后台管理系统</span>
    </a>

    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top">
        <!-- Sidebar toggle button-->
        <a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button">
            <span class="sr-only">Toggle navigation</span>
        </a>
        <!-- Navbar Right Menu -->
        <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
                <!-- Messages: style can be found in dropdown.less-->

                <!-- Notifications: style can be found in dropdown.less -->

                <!-- Tasks: style can be found in dropdown.less -->
                <!-- User Account: style can be found in dropdown.less -->
                <li class="dropdown user-menu" style="height:100%">
                    <!-- Single button -->
                    <div class="btn-group" style="margin-right:14px">
                        <button type="button" class="btn btn-default dropdown-toggle"
                                style="min-height:50px;background-color: transparent;border:none;"
                                data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <img src="${request.contextPath}/static/adminLTE/dist/img/user2-160x160.jpg" class="user-image"
                                 alt="User Image" style="height:35px;width:35px">
                            <span style="color:white;font-size:20px">Admin</span>
                        </button>
                        <button type="button" style="min-height:50px;background-color: transparent;border-right:none"
                                class="btn dropdown-toggle" data-toggle="dropdown" aria-haspopup="true"
                                aria-expanded="false">
                            <span class="caret" style="color:white"></span>
                        </button>
                        <ul class="dropdown-menu">
                            <li><a href="/admin/toChangePassword" class=".logout">修改密码</a></li>
                            <li><a href="/logout" class=".logout">退出登录</a></li>
                        </ul>
                    </div>
                </li>
            </ul>
        </div>

    </nav>
</header>
<#include "sidebar.ftl" />