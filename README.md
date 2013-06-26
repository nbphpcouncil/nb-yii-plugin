nb-yii-plugin
=============

Yii Framework plugin for NetBeans 7.3+

## Usage

### Install Plugin

Download a nbm file or create it by yourself. And please, install it.

### How to enable

- Use default directory structure (don't delete yiic* files)
- project properties > Framework > Yii, check `enabled` (You don't have to check this if you use plugin with default app)

### Existing Source

Source directory : Please, specify the webroot directory  
Webroot directory : Please, keep default (Source directory)

e.g.

    testdrive (webroot directory)
    ├── assets
    ├── css
    ├── images
    ├── index-test.php
    ├── index.php
    ├── nbproject
    ├── protected
    └── themes

### Existing Source (Source Directory has other directories)

Please set webroot directory(yii path alias named "webroot" i.e. webroot is testdrive on the below tree) to webroot of project properties.

`Right-click project > properties > Source > Webroot`

e.g.

    my_source (source directory)
    ├── testdrive (webroot directory)
    │   ├── assets
    │   ├── css
    │   ├── images
    │   ├── index-test.php
    │   ├── index.php
    │   ├── protected
    │   └── themes
    │ 
    ├── ...
    ├── foo
    └── bar

## Features

- Badge icon
- Go To Action
- Go To View
- Code Completion on the view file
- Code Completion for the path
- Init Action
- PHPUnit Test Init Action
- New Yii Project Wizard
- Run Action Action
- Navigation for path alias (Hyperlink to file)
- Run Command Action


### Go To Action

You can open the controller file and move action method.

e.g. WebRoot/protected/views/site/index.php -> WebRoot/protected/controllers/SiteController.php::actionIndex()

1. open a view file
2. Right-click on Editor
3. Navigate > Go To Action

### Go to View

You can open the view file for action method of the controller.(similar to Go To Action)

1. open a controller file
2. move the caret to action method
3. Right-click on Editor
4. Navigate > Go To View

If you set the keymap for this action, it's more useful.(Please, search with "php")  
If you set the theme on main.php, you will go to there.
```php
// protected/main.php
// e.g. set themes/basic
array(
    // something...
    'theme' => 'basic',
);
```

### Hyper link to view file

You can open the view file from parameter of render and renderPartial methods.
This is available on the Controller or View files.

e.g.
```php
public actionIndex() {
    // something ...

    $this->render('foo', array('bar' => $bar));
    $this->renderPartial('bar');
}
```
When you use the render method like above, if foo.php exists, you do the following.

#### fallback to default views

You can fallback to default views if view file for your theme doesn't exist when you use the theme.
If you would like to this feature, please, check "Fallback to ..." on project properties for Yii.


1. Hold down Ctrl key on the first parameter (foo)
2. Wait to be changed string color to blue
3. Click (foo)

It will go to theme file if you use theme.

### Navigation for path alias (Hyperlink to file)
Usage is the same as hyperlink to view file.

This feature is available for the followings:

- Yii::import()
- Yii::t()
- CBaseController::widget()
- CBaseController::beginWidget()
- CBaseController::endWidget()
- CBaseController::createWidget()
- CBaseController::beginCache()
- CBaseController::beginContent()

1. Hold down `Ctrl` on target path
2. string color is changed to blue if file exists(file path is displeyed as tooltip)
3. Click > open file

e.g.

```php
$this->widget('application.components.MyComponent', array());
// Hold down Ctrl on application.components.MyComponent

// support for the path that starts with '/' and '//'
$this->beginContent('//layouts/main');
$this->renderPartial('/users/foo');

// support for the class name
$this->widget('MyWidget');
```

Furthermore... Class name is also valid at the other places.  
e.g.

```php
return array(
    'some'=>array(
        // Hold-down Ctrl key on 'ClassName'
        'class'=>'SomeAction',
    ),
);
```

i18n
```php
Yii::t('Xyz.categoryName', 'message to be translated');
// on the first parameter : open the message file for LanguageID
// on the second parameter :  open the message file for LanguageID, and caret position is moved to specified message
```

#### Notice
- This feature works with default path alias name.
- Automatically file creation is valid with only render and renderPartial methods.

### Code Completion on the view file
Provide support for code completion on the View file.

e.g. webapp/protected/controllers/SiteController.php
```php
class SiteController extends Controller {
    // something...

    public function actionIndex(){
        // ...
        $this->render('foo', array(
            'var1' => $var,
            'var2' => 'bar',
        ));
    }
}
```

e.g. webapp/protected/views/site/index.php

```php
$ // [Ctrl + Space] popup $var1, $var2, $this, ...
$this-> // [Ctrl + Space] popup SiteController methods and fields
```
### Code Completion for the path
This is available the following methods:

- render
- renderPartial
- beginContent
- beginCache
- import
- *widget

e.g.

```php
// type character or [Ctrl + Space]
$this->render('i');

// class or path alias
$this->widget('application.[Ctrl + Space]');
```

### Init Action
Run the followings:

- Set framework directory path to Project Properties.
- Create a file for code completion.

**Project right-click > Yii > Init**

### PHPUnit Test Init Action
Set bootstrap.php and phpunit.xml to project properties.

**Project right-click > Yii > PHPUnit Test Init**

### New Yii Project Wizard

#### Set yiic.php

**Tools > Option > PHP > Yii**
Please set the path to YiiRoot/framework/yiic.php
(Browse... please choose the yiic.php file)

#### Create new project

1. File > New Project
2. Categories : PHP, Projects : PHP Application
3. Set Name and Location
4. Set Run Configuration
5. PHP Frameworks > Yii PHP Web Framework

Run the followings:

- If you check PHPUnit settings, Run PHPUnit Test Init Action.
- Create a file for code completion.
- Set include path.

### Run Action Action
Run action for current caret position. i.e. Open the browser.
If the action has some arguments, you have to set some arguments.(#9)

### Run Command Action
Support for yiic command.

- Right-click project > Yii > Run Command... or `Alt + Shift + X`

#### yiic.php
This action uses `protected/yiic.php`. If it doesn't exist, `Tools > Options > PHP > Yii > yii script` is used.
If it doesn't also exist, you can't run `Run Command`.

## Notice

We assume that yii project is used default directory structure. So some features don't work if you change directory names and structure.
For example, If you use `protected/configuration` instead of `protected/config`, Navigation for theme will be invalid.

## License
[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)
