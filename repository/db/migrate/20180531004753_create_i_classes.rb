class CreateIClasses < ActiveRecord::Migration[5.1]
  def change
    create_table :i_classes do |t|
      t.string :class_name
      t.string :qualified_name
      t.integer :method_count
      t.integer :attribute_count
      t.integer :lines_of_code
      t.string :hash
      t.references :package, foreign_key: true

      t.timestamps
    end
  end
end
