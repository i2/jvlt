<Global.Microsoft.VisualBasic.CompilerServices.DesignerGenerated()> _
Partial Public Class Standard
    Inherits System.Windows.Forms.Form

    'Das Formular überschreibt den Löschvorgang, um die Komponentenliste zu bereinigen.
    <System.Diagnostics.DebuggerNonUserCode()> _
    Protected Overrides Sub Dispose(ByVal disposing As Boolean)
        If disposing AndAlso components IsNot Nothing Then
            components.Dispose()
        End If
        MyBase.Dispose(disposing)
    End Sub

    'Wird vom Windows Form-Designer benötigt.
    Private components As System.ComponentModel.IContainer
    Private mainMenu1 As System.Windows.Forms.MainMenu

    'Hinweis: Die folgende Prozedur ist für den Windows Form-Designer erforderlich.
    'Das Bearbeiten ist mit dem Windows Form-Designer möglich.  
    'Das Bearbeiten mit dem Code-Editor ist nicht möglich.
    <System.Diagnostics.DebuggerStepThrough()> _
    Private Sub InitializeComponent()
        Me.mainMenu1 = New System.Windows.Forms.MainMenu
        Me.menOptions = New System.Windows.Forms.MenuItem
        Me.menStart = New System.Windows.Forms.MenuItem
        Me.menRepeat = New System.Windows.Forms.MenuItem
        Me.menSaveRepeat = New System.Windows.Forms.MenuItem
        Me.menShow = New System.Windows.Forms.MenuItem
        Me.OpenFileDialog1 = New System.Windows.Forms.OpenFileDialog
        Me.lblVocab = New System.Windows.Forms.Label
        Me.lblDict = New System.Windows.Forms.Label
        Me.lblLeccion = New System.Windows.Forms.Label
        Me.lblForeignVocab = New System.Windows.Forms.Label
        Me.cbxLesson = New System.Windows.Forms.ComboBox
        Me.lblChooseLesson = New System.Windows.Forms.Label
        Me.chkDirection = New System.Windows.Forms.CheckBox
        Me.btnPlus = New System.Windows.Forms.Button
        Me.btnMinus = New System.Windows.Forms.Button
        Me.SaveFileDialog1 = New System.Windows.Forms.SaveFileDialog
        Me.btnWrong = New System.Windows.Forms.Button
        Me.btnRight = New System.Windows.Forms.Button
        Me.Panel1 = New System.Windows.Forms.Panel
        Me.pgrVocabs = New System.Windows.Forms.ProgressBar
        Me.SuspendLayout()
        '
        'mainMenu1
        '
        Me.mainMenu1.MenuItems.Add(Me.menOptions)
        Me.mainMenu1.MenuItems.Add(Me.menShow)
        '
        'menOptions
        '
        Me.menOptions.MenuItems.Add(Me.menStart)
        Me.menOptions.MenuItems.Add(Me.menRepeat)
        Me.menOptions.MenuItems.Add(Me.menSaveRepeat)
        Me.menOptions.Text = "Options"
        '
        'menStart
        '
        Me.menStart.Text = "Startpage"
        '
        'menRepeat
        '
        Me.menRepeat.Text = "Load Repeat"
        '
        'menSaveRepeat
        '
        Me.menSaveRepeat.Text = "Save Repeat"
        '
        'menShow
        '
        Me.menShow.Text = "Show"
        '
        'OpenFileDialog1
        '
        Me.OpenFileDialog1.FileName = "OpenFileDialog1"
        Me.OpenFileDialog1.Filter = "VLT Dateien (*.jvlt)|*.jvlt"
        '
        'lblVocab
        '
        Me.lblVocab.Font = New System.Drawing.Font("Tahoma", 13.0!, System.Drawing.FontStyle.Regular)
        Me.lblVocab.Location = New System.Drawing.Point(21, 167)
        Me.lblVocab.Name = "lblVocab"
        Me.lblVocab.Size = New System.Drawing.Size(196, 74)
        Me.lblVocab.Text = "Vocab:"
        '
        'lblDict
        '
        Me.lblDict.Font = New System.Drawing.Font("Tahoma", 9.0!, System.Drawing.FontStyle.Bold)
        Me.lblDict.Location = New System.Drawing.Point(21, 3)
        Me.lblDict.Name = "lblDict"
        Me.lblDict.Size = New System.Drawing.Size(199, 20)
        Me.lblDict.Text = "Dictionary:"
        '
        'lblLeccion
        '
        Me.lblLeccion.Font = New System.Drawing.Font("Tahoma", 9.0!, System.Drawing.FontStyle.Bold)
        Me.lblLeccion.Location = New System.Drawing.Point(21, 73)
        Me.lblLeccion.Name = "lblLeccion"
        Me.lblLeccion.Size = New System.Drawing.Size(135, 20)
        Me.lblLeccion.Text = "Leccion:"
        '
        'lblForeignVocab
        '
        Me.lblForeignVocab.Font = New System.Drawing.Font("Tahoma", 13.0!, System.Drawing.FontStyle.Regular)
        Me.lblForeignVocab.Location = New System.Drawing.Point(21, 101)
        Me.lblForeignVocab.Name = "lblForeignVocab"
        Me.lblForeignVocab.Size = New System.Drawing.Size(199, 62)
        Me.lblForeignVocab.Text = "Foreign"
        '
        'cbxLesson
        '
        Me.cbxLesson.Location = New System.Drawing.Point(21, 33)
        Me.cbxLesson.Name = "cbxLesson"
        Me.cbxLesson.Size = New System.Drawing.Size(199, 22)
        Me.cbxLesson.TabIndex = 7
        '
        'lblChooseLesson
        '
        Me.lblChooseLesson.Font = New System.Drawing.Font("Tahoma", 9.0!, System.Drawing.FontStyle.Bold)
        Me.lblChooseLesson.Location = New System.Drawing.Point(21, 18)
        Me.lblChooseLesson.Name = "lblChooseLesson"
        Me.lblChooseLesson.Size = New System.Drawing.Size(100, 14)
        Me.lblChooseLesson.Text = "Choose Lesson:"
        '
        'chkDirection
        '
        Me.chkDirection.Location = New System.Drawing.Point(103, 55)
        Me.chkDirection.Name = "chkDirection"
        Me.chkDirection.Size = New System.Drawing.Size(134, 20)
        Me.chkDirection.TabIndex = 12
        Me.chkDirection.Text = "Change Direction"
        '
        'btnPlus
        '
        Me.btnPlus.Font = New System.Drawing.Font("Tahoma", 16.0!, System.Drawing.FontStyle.Bold)
        Me.btnPlus.Location = New System.Drawing.Point(165, 71)
        Me.btnPlus.Name = "btnPlus"
        Me.btnPlus.Size = New System.Drawing.Size(29, 25)
        Me.btnPlus.TabIndex = 18
        Me.btnPlus.Text = "+"
        '
        'btnMinus
        '
        Me.btnMinus.Font = New System.Drawing.Font("Tahoma", 16.0!, System.Drawing.FontStyle.Bold)
        Me.btnMinus.Location = New System.Drawing.Point(200, 71)
        Me.btnMinus.Name = "btnMinus"
        Me.btnMinus.Size = New System.Drawing.Size(29, 25)
        Me.btnMinus.TabIndex = 19
        Me.btnMinus.Text = "-"
        '
        'SaveFileDialog1
        '
        Me.SaveFileDialog1.Filter = "VLT Dateien (*.jvlt)|*.jvlt"
        '
        'btnWrong
        '
        Me.btnWrong.Location = New System.Drawing.Point(8, 245)
        Me.btnWrong.Name = "btnWrong"
        Me.btnWrong.Size = New System.Drawing.Size(111, 22)
        Me.btnWrong.TabIndex = 25
        Me.btnWrong.Text = "Wrong"
        '
        'btnRight
        '
        Me.btnRight.Location = New System.Drawing.Point(122, 245)
        Me.btnRight.Name = "btnRight"
        Me.btnRight.Size = New System.Drawing.Size(111, 22)
        Me.btnRight.TabIndex = 26
        Me.btnRight.Text = "Right"
        '
        'Panel1
        '
        Me.Panel1.BackColor = System.Drawing.Color.White
        Me.Panel1.Location = New System.Drawing.Point(8, 101)
        Me.Panel1.Name = "Panel1"
        Me.Panel1.Size = New System.Drawing.Size(13, 22)
        '
        'pgrVocabs
        '
        Me.pgrVocabs.Location = New System.Drawing.Point(21, 60)
        Me.pgrVocabs.Name = "pgrVocabs"
        Me.pgrVocabs.Size = New System.Drawing.Size(84, 10)
        '
        'Standard
        '
        Me.AutoScaleDimensions = New System.Drawing.SizeF(96.0!, 96.0!)
        Me.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi
        Me.AutoScroll = True
        Me.ClientSize = New System.Drawing.Size(240, 268)
        Me.Controls.Add(Me.pgrVocabs)
        Me.Controls.Add(Me.Panel1)
        Me.Controls.Add(Me.btnRight)
        Me.Controls.Add(Me.btnWrong)
        Me.Controls.Add(Me.btnMinus)
        Me.Controls.Add(Me.btnPlus)
        Me.Controls.Add(Me.chkDirection)
        Me.Controls.Add(Me.lblChooseLesson)
        Me.Controls.Add(Me.cbxLesson)
        Me.Controls.Add(Me.lblForeignVocab)
        Me.Controls.Add(Me.lblLeccion)
        Me.Controls.Add(Me.lblDict)
        Me.Controls.Add(Me.lblVocab)
        Me.Menu = Me.mainMenu1
        Me.Name = "Standard"
        Me.Text = "wmVLT"
        Me.ResumeLayout(False)

    End Sub
    Friend WithEvents menOptions As System.Windows.Forms.MenuItem
    Friend WithEvents OpenFileDialog1 As System.Windows.Forms.OpenFileDialog
    Friend WithEvents lblVocab As System.Windows.Forms.Label
    Friend WithEvents lblDict As System.Windows.Forms.Label
    Friend WithEvents lblLeccion As System.Windows.Forms.Label
    Friend WithEvents lblForeignVocab As System.Windows.Forms.Label
    Friend WithEvents cbxLesson As System.Windows.Forms.ComboBox
    Friend WithEvents lblChooseLesson As System.Windows.Forms.Label
    Friend WithEvents menShow As System.Windows.Forms.MenuItem
    Friend WithEvents chkDirection As System.Windows.Forms.CheckBox
    Friend WithEvents btnPlus As System.Windows.Forms.Button
    Friend WithEvents btnMinus As System.Windows.Forms.Button
    Friend WithEvents menStart As System.Windows.Forms.MenuItem
    Friend WithEvents menRepeat As System.Windows.Forms.MenuItem
    Friend WithEvents menSaveRepeat As System.Windows.Forms.MenuItem
    Friend WithEvents SaveFileDialog1 As System.Windows.Forms.SaveFileDialog
    Friend WithEvents btnWrong As System.Windows.Forms.Button
    Friend WithEvents btnRight As System.Windows.Forms.Button
    Friend WithEvents Panel1 As System.Windows.Forms.Panel
    Friend WithEvents pgrVocabs As System.Windows.Forms.ProgressBar

End Class
