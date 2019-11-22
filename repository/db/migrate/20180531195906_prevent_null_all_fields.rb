class PreventNullAllFields < ActiveRecord::Migration[5.1]
  def change
    # Project

    change_column_null :projects, :project_name, false
    change_column_null :projects, :num_packages, false

    # Package

    change_column_null :packages, :package_name, false
    change_column_null :packages, :class_count, false
    change_column_null :packages, :has_subpackages, false
    change_column_null :packages, :project_id, false

    # IClass

    change_column_null :i_classes, :class_name, false
    change_column_null :i_classes, :qualified_name, false
    change_column_null :i_classes, :method_count, false
    change_column_null :i_classes, :attribute_count, false
    change_column_null :i_classes, :lines_of_code, false
    change_column_null :i_classes, :hash, false
    change_column_null :i_classes, :package_id, false

    # Attribute

    change_column_null :attributes, :attribute_name, false
    change_column_null :attributes, :type, false
    change_column_null :attributes, :i_class_id, false

    # IMethod

    rename_column :i_methods, :name, :method_name 
    change_column_null :i_methods, :method_name, false
    change_column_null :i_methods, :start_of_method, false
    change_column_null :i_methods, :length_of_method, false
    change_column_null :i_methods, :lines_of_code, false
    change_column_null :i_methods, :return_type, false
    change_column_null :i_methods, :argument_count, false
    change_column_null :i_methods, :key, false
    change_column_null :i_methods, :i_class_id, false

    # MethodInvocation

    change_column_null :method_invocations, :invocation, false
    change_column_null :method_invocations, :i_method_id, false

    # Argument

    change_column_null :arguments, :argument_name, false
    change_column_null :arguments, :type, false
    change_column_null :arguments, :i_method_id, false


  end
end
