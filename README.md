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

## License

## TODO

- add license

