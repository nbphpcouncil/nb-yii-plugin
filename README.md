nb-yii-plugin
=============

Yii Framework plugin for NetBeans 7.3+

## Usage

### Install Plugin

Download a nbm file or create it yourself. And please, install it.

### Existing Source

Project directory : Please, specify the webapp directory

e.g.

    testdrive (webapp directory)
    ├── assets
    ├── css
    ├── images
    ├── index-test.php
    ├── index.php
    ├── nbproject
    ├── protected
    └── themes

## Features

- Badge icon
- Go To Action
- Go To View
- Code Completion on the view file
- Init Action
- PHPUnit Test Init Action
- New Yii Project Wizard


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

### Hyper link to view file

You can open the view file from parameter of render method.

e.g.
```php
public actionIndex() {
    // something ...

    $this->render('foo', array('bar' => $bar));
}
```
When you use the render method like above, if foo.php exists, you do the following.

1. Hold down Ctrl key on the first parameter (foo)
2. Wait to be changed string color to blue
3. Click (foo)

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

## License

## TODO

- add license
- code completion for widget
